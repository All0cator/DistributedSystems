package Nodes;

import Actions.ActionsForCustomerApp;
import Primitives.HostData;
import Primitives.Payloads.RegistrationPayload;

public class CustomerApp extends Node {

    private HostData masterHostData;
    public static void main(String[] args) {
        if(args.length != 4) return;

        new CustomerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();
    }

    public CustomerApp(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForCustomerApp();
        this.masterHostData = new HostData(masterHostIP, masterPort);
    }

    @Override
    public void Start() {
        this.actions.GetTotalCount(masterHostData, hostData);
    }
}