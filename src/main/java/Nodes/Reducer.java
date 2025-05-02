package Nodes;

import java.util.ArrayList;

import Actions.ActionsForMaster;
import Actions.ActionsForReducer;
import Primitives.AtomicF;
import Primitives.AtomicI;
import Primitives.AtomicS;
import Primitives.HostData;
import Primitives.ReductionCompletionData;
import Primitives.Store;
import Primitives.Payloads.RegistrationPayload;

public class Reducer extends Node{

    private HostData masterHostData;

    private AtomicF totalCounts[];
    private AtomicS totalStores[];
    private AtomicI workersRemainingCounters[];

    public static void main(String[] args) {
        if(args.length != 4) return;

        new Reducer(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();
    }

    public Reducer(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForReducer();
        this.masterHostData = new HostData(masterHostIP, masterPort);

        this.totalCounts = new AtomicF[100];
        this.totalStores = new AtomicS[100];
        this.workersRemainingCounters = new AtomicI[100];
        for(int i = 0; i < 100; ++i) {
            this.totalCounts[i] = new AtomicF();
            this.totalStores[i] = new AtomicS();
            this.workersRemainingCounters[i] = new AtomicI();
            this.workersRemainingCounters[i].SetValue(-1);
        }
    }

    public synchronized HostData GetMasterHostData() {
        return new HostData(this.masterHostData);
    }

    public ReductionCompletionData ReductionCompletion(int mapID) {
        if(this.workersRemainingCounters[mapID].GetValue() > 0) {
            return null;
        }

        ReductionCompletionData data = new ReductionCompletionData();
        data.totalCount = this.totalCounts[mapID].GetValue();

        this.totalCounts[mapID].SetValue(0.0f);
        this.workersRemainingCounters[mapID].SetValue(-1);

        return data;
    }

    public ArrayList<Store> StoreReductionCompletion(int mapID) {
        if(this.workersRemainingCounters[mapID].GetValue() > 0) {
            return null;
        }

        ArrayList<Store> stores = this.totalStores[mapID].GetValue();
        this.totalStores[mapID].SetValue(new ArrayList<Store>());

        this.workersRemainingCounters[mapID].SetValue(-1);

        return stores;
    }

    public void Reduce(int mapID, int numWorkers, ArrayList<Store> totalStores) {
        if(this.workersRemainingCounters[mapID].GetValue() == -1) {
            this.workersRemainingCounters[mapID].SetValue(numWorkers);
        }

        for(int i = 0; i < totalStores.size(); ++i) {
            this.totalStores[mapID].Add(totalStores.get(i));
        }

        // Finished with reduction of worker
        this.workersRemainingCounters[mapID].Add(-1);
    }
 
    public void Reduce(int mapID, int numWorkers, float totalCount[]) {
        if(this.workersRemainingCounters[mapID].GetValue() == -1) {
            this.workersRemainingCounters[mapID].SetValue(numWorkers);
        }

        for(int i = 0; i < totalCount.length; ++i) {
            this.totalCounts[mapID].Add(totalCount[i]);
        }

        // Finished with reduction of worker
        this.workersRemainingCounters[mapID].Add(-1);
    }

    @Override
    public void Start() {
        RegistrationPayload p = new RegistrationPayload();
        p.isWorkerNode = false;
        p.hostData = hostData;

        this.actions.RegisterNodeToMaster(this.masterHostData, p);
    }
}
