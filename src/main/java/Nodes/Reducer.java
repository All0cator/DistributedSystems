package Nodes;

import Actions.ActionsForMaster;
import Actions.ActionsForReducer;
import Primitives.HostData;
import Primitives.Payloads.RegistrationPayload;

public class Reducer extends Node{

    private HostData masterHostData;

    public static void main(String[] args) {
        if(args.length != 4) return;

        new Reducer(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();
    }

    public Reducer(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForReducer();
        this.masterHostData = new HostData(masterHostIP, masterPort);
    }

    @Override
    public void Start() {
        RegistrationPayload p = new RegistrationPayload();
        p.isWorkerNode = false;
        p.hostData = hostData;

        this.actions.RegisterNodeToMaster(this.masterHostData, p);
    }
}
