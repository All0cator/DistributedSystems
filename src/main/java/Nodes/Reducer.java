package Nodes;

import java.io.IOException;
import java.sql.PseudoColumnUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Actions.ActionsForMaster;
import Actions.ActionsForReducer;
import Primitives.AtomicF;
import Primitives.AtomicI;
import Primitives.AtomicMS;
import Primitives.AtomicMapStof;
import Primitives.AtomicS;
import Primitives.AtomicStr;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.ReductionCompletionData;
import Primitives.Store;
import Primitives.Payloads.ManagerStatePayload;
import Primitives.Payloads.RegistrationPayload;

public class Reducer extends Node{

    private HostData masterHostData;

    private AtomicF totalCounts[];
    private AtomicS totalStores[];
    private AtomicI workersRemainingCounters[];
    private AtomicMapStof totalTypesToRevenuesMaps[];
    private AtomicMS totalManagerStates[];
    private AtomicStr totalFoodCategories[];

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
        this.totalTypesToRevenuesMaps = new AtomicMapStof[100];
        this.totalManagerStates = new AtomicMS[100];
        this.totalFoodCategories = new AtomicStr[100];

        for(int i = 0; i < 100; ++i) {
            this.totalCounts[i] = new AtomicF();
            this.totalStores[i] = new AtomicS();
            this.workersRemainingCounters[i] = new AtomicI();
            this.totalTypesToRevenuesMaps[i] = new AtomicMapStof();
            this.totalManagerStates[i] = new AtomicMS();
            this.totalFoodCategories[i] = new AtomicStr();
            this.workersRemainingCounters[i].SetValue(-1);
        }
    }

    public synchronized HostData GetMasterHostData() {
        return new HostData(this.masterHostData);
    }

    public synchronized ReductionCompletionData ReductionCompletion(int mapID) {
        if(this.workersRemainingCounters[mapID].GetValue() > 0) {
            return null;
        }

        ReductionCompletionData data = new ReductionCompletionData();
        data.totalCount = this.totalCounts[mapID].GetValue();

        this.totalCounts[mapID].SetValue(0.0f);
        this.workersRemainingCounters[mapID].SetValue(-1);

        return data;
    }

    public synchronized Set<String> FoodCategoriesReductionCompletion(int mapID) {
        if(this.workersRemainingCounters[mapID].GetValue() > 0) {
            return null;
        }

        Set<String> result = this.totalFoodCategories[mapID].GetValue();
        this.totalFoodCategories[mapID].SetValue(new HashSet<String>());
        this.workersRemainingCounters[mapID].SetValue(-1);

        return result;
    }

    public synchronized void ReduceFoodCategories(int mapID, int numWorkers, Set<String> foodCategories) {
        if(this.workersRemainingCounters[mapID].GetValue() == -1) {
            this.workersRemainingCounters[mapID].SetValue(numWorkers);
        }

        for(String foodCategory : foodCategories) {
            this.totalFoodCategories[mapID].Add(foodCategory);
        }

        this.workersRemainingCounters[mapID].Add(-1);
    }

    public synchronized void ReduceManagerState(ManagerStatePayload pState) {
        if(this.workersRemainingCounters[pState.mapID].GetValue() == -1) {
            this.workersRemainingCounters[pState.mapID].SetValue(pState.numWorkers);
        }

        for(String foodCategory : pState.foodCategories) {
            this.totalManagerStates[pState.mapID].AddFoodCategory(foodCategory);
        }

        for(String productType : pState.productTypes) {
            this.totalManagerStates[pState.mapID].AddProductType(productType);
        }

        for(Store store : pState.stores) {
            this.totalManagerStates[pState.mapID].AddStore(store);
        }

        // Finished with reduction of worker
        this.workersRemainingCounters[pState.mapID].Add(-1);
    }

    public synchronized ManagerStatePayload ManagerStateReductionCompletion(int mapID) {
        if(this.workersRemainingCounters[mapID].GetValue() > 0) {
            return null;
        }

        ManagerStatePayload pState = new ManagerStatePayload();
        pState.foodCategories = new HashSet<String>();
        pState.productTypes = new HashSet<String>();
        pState.stores = new ArrayList<Store>();
        pState.mapID = mapID;

        this.totalManagerStates[mapID].GetFoodCategories(pState.foodCategories);
        this.totalManagerStates[mapID].GetProductTypes(pState.productTypes);
        this.totalManagerStates[mapID].GetStores(pState.stores);

        this.totalManagerStates[mapID].SetFoodCategories(new HashSet<String>());
        this.totalManagerStates[mapID].SetProductTypes(new HashSet<String>());
        this.totalManagerStates[mapID].SetStores(new ArrayList<Store>());

        this.workersRemainingCounters[mapID].SetValue(-1);

        return pState;
    }

    public synchronized ArrayList<Store> StoreReductionCompletion(int mapID) {
        if(this.workersRemainingCounters[mapID].GetValue() > 0) {
            return null;
        }

        ArrayList<Store> stores = this.totalStores[mapID].GetValue();
        this.totalStores[mapID].SetValue(new ArrayList<Store>());

        this.workersRemainingCounters[mapID].SetValue(-1);

        return stores;
    }

    public synchronized HashMap<String, Float> RevenueByTypeReductionCompletion(int mapID) {
        if(this.workersRemainingCounters[mapID].GetValue() > 0) {
            return null;
        }

        HashMap<String, Float> revenues = this.totalTypesToRevenuesMaps[mapID].GetValue();
        this.totalTypesToRevenuesMaps[mapID].SetValue(new HashMap<String, Float>());

        this.workersRemainingCounters[mapID].SetValue(-1);

        return revenues;
    }

    // java erases generics at runtime so i have to use another name to differentiate the signature
    public synchronized void ReduceRevenueByType(int mapID, int numWorkers, HashMap<String, Float> typeToRevenue) {
        if(this.workersRemainingCounters[mapID].GetValue() == -1) {
            this.workersRemainingCounters[mapID].SetValue(numWorkers);
        }

        for(Map.Entry<String, Float> e : typeToRevenue.entrySet()) {
            this.totalTypesToRevenuesMaps[mapID].Add(e.getKey(), e.getValue());
        }

        // Finished with reduction of worker
        this.workersRemainingCounters[mapID].Add(-1);
    }

    public synchronized void Reduce(int mapID, int numWorkers, ArrayList<Store> totalStores) {
        if(this.workersRemainingCounters[mapID].GetValue() == -1) {
            this.workersRemainingCounters[mapID].SetValue(numWorkers);
        }

        for(int i = 0; i < totalStores.size(); ++i) {
            this.totalStores[mapID].Add(totalStores.get(i));
        }

        // Finished with reduction of worker
        this.workersRemainingCounters[mapID].Add(-1);
    }
 
    public synchronized void Reduce(int mapID, int numWorkers, float totalCount[]) {
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

        Message msg = new Message();
        msg.type = MessageType.REGISTER_NODE;
        RegistrationPayload p = new RegistrationPayload();
        msg.payload = p;

        p.isWorkerNode = false;
        p.hostData = hostData;

        
        try {
            this.actions.SendMessageToNode(this.masterHostData, msg);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
