package Nodes;
import java.util.ArrayList;

import Actions.ActionsForMaster;
import Primitives.HostData;
import Primitives.RequestPool;

public class Master extends Node{
    
    private HostData reducerHostData;

    private ArrayList<HostData> workerHostDatas;

    public RequestPool requestPool;

    public Master(String hostIP, int port) {
        super(hostIP, port);
        this.actions = new ActionsForMaster();
        this.workerHostDatas = new ArrayList<HostData>();
        this.requestPool = new RequestPool(100);

        this.reducerHostData = new HostData("", -1);
    }

    public synchronized int RegisterWorker(HostData workerHostData) {
        workerHostDatas.add(workerHostData);
        return workerHostDatas.size() - 1;
    }


    public synchronized void RegisterReducer(HostData reducerHostData) {
        this.reducerHostData = reducerHostData;
    }

    // Immutable State snapshot 
    public synchronized ArrayList<HostData> GetWorkerHostDatas() {
        return new ArrayList<HostData>(this.workerHostDatas);
    }

    public synchronized int GetWorkerCount() {
        return workerHostDatas.size();
    }

    public synchronized HostData GetReducerHostData() {
        return new HostData(this.reducerHostData);
    }

    public static void main(String[] args) {
        if(args.length != 2) return;
        

        new Master(args[0], Integer.parseInt(args[1])).start();
    }
}
