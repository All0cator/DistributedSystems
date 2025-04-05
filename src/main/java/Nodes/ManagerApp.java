package Nodes;

import Actions.ActionsForManagerApp;
import Primitives.HostData;
import Primitives.Payloads.RegistrationPayload;

public class ManagerApp extends Node {
    private HostData masterHostData;

    public static void main(String[] args) {
        if(args.length != 4) return;

        new ManagerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();
    }

    public ManagerApp(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForManagerApp();
        this.masterHostData = new HostData(masterHostIP, masterPort);
    }

    @Override
    public void Start() {
        this.actions.GetTotalCount(masterHostData, hostData);
    }
}
