package Nodes;

import Actions.ActionsForMaster;
import Actions.ActionsForWorker;
import Primitives.HostData;
import Primitives.Payloads.HostDiscoveryRequestMasterPayload;
import Primitives.Payloads.RegistrationPayload;

public class Worker extends Node {

    private HostData masterHostData;
    private HostData reducerHostData;
    
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

    public synchronized int GetMasterPort() {
        return this.masterHostData.GetPort();
    }

    public synchronized void RegisterReducer(int reducerPort, String reducerHostIP) {
        this.reducerHostData.SetPort(reducerPort);
        this.reducerHostData.SetHostIP(reducerHostIP);
    }

    public synchronized int GetReducerPort() {
        return this.reducerHostData.GetPort();
    }

    public synchronized String GetReducerHostIP() {
        return this.reducerHostData.GetHostIP();
    }

    @Override
    public void Start() {
        RegistrationPayload p = new RegistrationPayload();
        p.isWorkerNode = true;
        p.hostData = hostData;

        this.actions.RegisterNodeToMaster(this.masterHostData, p);
    }
}
