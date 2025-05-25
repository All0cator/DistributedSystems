package Nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

import Actions.ActionsForMaster;
import Actions.ActionsForWorker;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Product;
import Primitives.Purchase;
import Primitives.Store;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.JSONStoresPayload;
import Primitives.Payloads.RegistrationPayload;

public class Worker extends Node {

    // assume we have 4 workers

    public static float workersData[][] = {
        {1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 9.0f, 10.0f}, // 55
        {20.0f, 10.0f, 3.0f, 10.0f, 3.0f, 5.0f, 5.0f, 3.0f, 3.0f, 3.0f}, // 65
        {8.0f, 2.0f, 1.0f, 2.0f, 3.0f, 5.0f, 5.0f, 15.0f, 10.0f, 30.0f}, // 81
        {3.0f, 5.0f, 8.0f, 7.0f, 1.0f, 3.0f, 3.0f, 3.0f, 4.0f, 6.0f} // 43
    };

    private HashMap<String, Store> storeNameToStore;

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
        this.storeNameToStore = new HashMap<String, Store>();
    }

    public synchronized HashMap<String, Float> GetTotalRevenuePerFoodCategory(String type) {
        HashMap<String, Float> result = new HashMap<String, Float>();

        result.put("total", 0.0f);

        for(Store s : this.storeNameToStore.values()) {
            if(s.GetFoodCategory().equals(type)) {
                result.put(s.GetName(), s.GetTotalRevenue());
                result.put("total", result.get("total") + s.GetTotalRevenue());
            }
        }

        return result;
    }

    public synchronized HashMap<String, Float> GetTotalRevenuePerProductType(String type) {
        HashMap<String, Float> result = new HashMap<String, Float>();

        result.put("total", 0.0f);

        for(Store s : this.storeNameToStore.values()) {
            result.put(s.GetName(), s.GetTotalRevenue(type));
            result.put("total", result.get("total") + s.GetTotalRevenue(type));
        }

        return result;
    }

    public synchronized void GetManagerState(Set<String> foodCategories, Set<String> productTypes, ArrayList<Store> stores) {
        for(Store store : this.storeNameToStore.values()) {
            stores.add(store);
            foodCategories.add(store.GetFoodCategory());

            for(Product product : store.GetProducts(false)) {
                productTypes.add(product.GetType());
            }
        }
    }

    public synchronized void GetFoodCategories(Set<String> foodCategories) {
        for(Store store : this.storeNameToStore.values()) {
            foodCategories.add(store.GetFoodCategory());
        }
    }

    public synchronized boolean AddStore(Store store) {

        if(!this.storeNameToStore.containsKey(store.GetName())) {
            this.storeNameToStore.put(store.GetName(), store);
            
            return true;
        }

        return false;
    }

    public synchronized ArrayList<Store> GetStores() {
        ArrayList<Store> result = new ArrayList<Store>();

        for(Store s : this.storeNameToStore.values()) {
            result.add(s);
        }

        return result;
    }

    public synchronized void DebugStores() {
        for(Store s : this.storeNameToStore.values()) {
            System.out.println(s);
        }
    }

    public synchronized boolean RateStore(String storeName, int noOfStars) {
        Store s = this.storeNameToStore.get(storeName);

        if(s != null) {
            s.Rate(noOfStars);
            System.out.println(s.GetStars());
        }


        return s != null;
    }

    public synchronized void RestockProduct(String storeName, String productName, int restockValue) {
        Store s = this.storeNameToStore.get(storeName);

        if(s != null) {
            int newAvailableAmmount = s.Restock(productName, restockValue);
            if(newAvailableAmmount != -1) {
                System.out.printf("Store: %s Product: %s Stock: %d\n", storeName, productName, newAvailableAmmount);
            }
        }
    }

    public synchronized void ToggleProductVisibility(String storeName, String productName, boolean isCustomerVisible) {
        Store s = this.storeNameToStore.get(storeName);

        if(s != null) {
            Boolean newCustomerVisibility = s.ToggleVisibility(productName, isCustomerVisible);
            if(newCustomerVisibility != null) {
                System.out.printf("Store: %s Product: %s Product Visibility: %b\n", storeName, productName, isCustomerVisible);
            }
        }
    }

    public synchronized boolean PurchaseFromStore(Purchase purchase) {
        Store s = this.storeNameToStore.get(purchase.storeName);

        boolean result = false;

        if(s != null) {
            result = s.MakePurchase(purchase); 
        }

        return result;
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

        Message msg = new Message();
        msg.type = MessageType.REGISTER_NODE;
        RegistrationPayload p = new RegistrationPayload();
        msg.payload = p;

        p.isWorkerNode = true;
        p.hostData = hostData;

        try {
            this.actions.SendMessageToNode(this.masterHostData, msg);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
