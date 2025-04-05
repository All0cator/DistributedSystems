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

    public synchronized void RegisterWorker(HostData workerHostData) {
        workerHostDatas.add(workerHostData);
        for(int i = 0; i < this.workerHostDatas.size(); ++i) {
            System.out.println(this.workerHostDatas.get(i).GetHostIP() + Integer.toString(this.workerHostDatas.get(i).GetPort()));
        }
    }


    public synchronized void RegisterReducer(HostData reducerHostData) {
        this.reducerHostData = reducerHostData;
        System.out.println(this.reducerHostData.GetHostIP() + Integer.toString(this.reducerHostData.GetPort()));
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
        
        System.out.println("Master Run!");

        new Master(args[0], Integer.parseInt(args[1])).start();
    }
}
