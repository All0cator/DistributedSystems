package Nodes;

import java.util.HashMap;

import Actions.ActionsForMaster;
import Actions.ActionsForWorker;
import Primitives.HostData;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.RegistrationPayload;

public class Worker extends Node {

    // assume we have 4 workers with 10 counters each

    public static float workersData[][] = {
        {1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f}, // 55
        {20.0f, 10.0f, 3.0f, 10.0f, 3.0f, 5.0f, 5.0f, 3.0f, 3.0f, 3.0f}, // 65
        {8.0f, 2.0f, 1.0f, 2.0f, 3.0f, 5.0f, 5.0f, 15.0f, 10.0f, 30.0f}, // 81
        {3.0f, 5.0f, 8.0f, 7.0f, 1.0f, 3.0f, 3.0f, 3.0f, 4.0f, 6.0f} // 43
    };

    // reduced ammount = 244

    private HostData masterHostData;
    private HostData reducerHostData;

    private int ID;
    
    public static void main(String[] args) {
        if(args.length != 4) return;

        new Worker(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();
    }

    public Worker(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForWorker();
        this.masterHostData = new HostData(masterHostIP, masterPort);
        this.reducerHostData = new HostData("", -1);
    }

    public synchronized HostData GetMasterHostData() {
        return new HostData(this.masterHostData);
    }

    public synchronized void RegisterReducer(int reducerPort, String reducerHostIP) {
        this.reducerHostData.SetPort(reducerPort);
        this.reducerHostData.SetHostIP(reducerHostIP);
    }

    public synchronized HostData GetReducerHostData() {
        return new HostData(this.reducerHostData);
    }

    public synchronized void SetReducerHostData(HostData reducerHostData) {
        this.reducerHostData = reducerHostData; 
    }

    public synchronized void SetID(int ID) {
        this.ID = ID;
    }

    public synchronized int GetID() {
        return this.ID;
    }

    @Override
    public void Start() {
        RegistrationPayload p = new RegistrationPayload();
        p.isWorkerNode = true;
        p.hostData = hostData;

        this.actions.RegisterNodeToMaster(this.masterHostData, p);
    }
}
