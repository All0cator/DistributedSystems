package Nodes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import Actions.ActionsForMaster;
import Primitives.HostData;
import Primitives.RequestPool;

public class Master extends Node{

    // should be launch argument 
    public static int MAX_WORKERS = 4;

    // 37.99625, 23.73303
    // 37.97492, 23.73430
    // 37.96234, 23.72528
    // 37.97543, 23.73355
    // 37.96825, 23.73158
    // 37.97598, 23.73376
    // 37.99871, 23.73936
    
    private HostData reducerHostData;

    private ArrayList<HostData> workerHostDatas;

    public RequestPool requestPool;

    private Set<String> foodCategories;

    public Master(String hostIP, int port) {
        super(hostIP, port);
        this.actions = new ActionsForMaster();
        this.workerHostDatas = new ArrayList<HostData>();
        this.requestPool = new RequestPool(100);

        this.reducerHostData = new HostData("", -1);

        this.foodCategories = new HashSet<String>();

        for(int i = 0; i < jsonStores.length; ++i) {
            this.foodCategories.add(jsonStores[i].getString("FoodCategory"));
        }
    }

    public synchronized void GetFoodCategories(ArrayList<String> foodCategories) {
        Set<String> result = new HashSet<String>();

        result.addAll(this.foodCategories);

        for(String foodCategory : result) {
            foodCategories.add(foodCategory);
        }
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

        ArrayList<HostData> result = new ArrayList<HostData>();

        for(int i = 0; i < this.workerHostDatas.size(); ++i) {
            result.add(new HostData(this.workerHostDatas.get(i)));
        }

        return result;
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

    public int StoreNameToWorkerID(String storeName) {
        return Math.abs(storeName.hashCode() % MAX_WORKERS);
    }

    public String[] GetStoresFromMemory(int workerID) {
        ArrayList<String> result = new ArrayList<String>();

        for(JSONObject store : jsonStores) {
            // there are negative hashCodes in java lol
            if(StoreNameToWorkerID(store.getString("StoreName")) == workerID) {
                result.add(store.toString());
            }
        }

        return result.toArray(new String[0]);
    }

    JSONObject jsonStores[] = {
        new JSONObject("{\r\n" + //
                        "    \"StoreName\": \"Domino's\",\r\n" + //
                        "    \"Latitude\": 37.99625,\r\n" + //
                        "    \"Longitude\": 23.73303,\r\n" + //
                        "    \"FoodCategory\": \"pizzeria\",\r\n" + //
                        "    \"Stars\": 4,\r\n" + //
                        "   \"NoOfVotes\": 444,\r\n" + //
                        "    \"StoreLogo\": \"src/main/resources/dominos_logo.png\",\r\n" + //
                        "   \"Products\": [\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"margarita\",\r\n" + //
                        "   \"ProductType\": \"pizza\",\r\n" + //
                        "   \"Available Amount\": 5000,\r\n" + //
                        "    \"Price\": 9.2\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"special\",\r\n" + //
                        "   \"ProductType\": \"pizza\",\r\n" + //
                        "   \"Available Amount\": 1000,\r\n" + //
                        "    \"Price\": 12.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"chef's Salad\",\r\n" + //
                        "   \"ProductType\": \"salad\",\r\n" + //
                        "   \"Available Amount\": 100,\r\n" + //
                        "    \"Price\": 5.0\r\n" + //
                        "   }\r\n" + //
                        "    ]\r\n" + //
                        "}"),
        new JSONObject("{\r\n" + //
                        "    \"StoreName\": \"Goody's\",\r\n" + //
                        "    \"Latitude\": 37.97492,\r\n" + //
                        "    \"Longitude\": 23.73430,\r\n" + //
                        "    \"FoodCategory\": \"burger\",\r\n" + //
                        "    \"Stars\": 4,\r\n" + //
                        "   \"NoOfVotes\": 2260,\r\n" + //
                        "    \"StoreLogo\": \"src/main/resources/goodys_logo.png\",\r\n" + //
                        "   \"Products\": [\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"philly burger\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 1000,\r\n" + //
                        "    \"Price\": 6.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"hamburger\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 2000,\r\n" + //
                        "    \"Price\": 4.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"cheeseburger\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 100,\r\n" + //
                        "    \"Price\": 3.0\r\n" + //
                        "   }\r\n" + //
                        "    ]\r\n" + //
                        "}\r\n" + //
                        "   "),
        new JSONObject("{\r\n" + //
                        "    \"StoreName\": \"Jackaroo\",\r\n" + //
                        "    \"Latitude\": 37.96234,\r\n" + //
                        "    \"Longitude\": 23.72528,\r\n" + //
                        "    \"FoodCategory\": \"fast food\",\r\n" + //
                        "    \"Stars\": 5,\r\n" + //
                        "   \"NoOfVotes\": 1647,\r\n" + //
                        "    \"StoreLogo\": \"src/main/resources/jackaroo_logo.png\",\r\n" + //
                        "   \"Products\": [\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"cheeseburger\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 5000,\r\n" + //
                        "    \"Price\": 2.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"hamburger\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 1000,\r\n" + //
                        "    \"Price\": 3.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"game over\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 200,\r\n" + //
                        "    \"Price\": 5.4\r\n" + //
                        "   }\r\n" + //
                        "    ]\r\n" + //
                        "}\r\n" + //
                        "   "),
        new JSONObject("{\r\n" + // 
                        "    \"StoreName\": \"KFC\",\r\n" + //
                        "    \"Latitude\": 37.97543, \r\n" + //
                        "    \"Longitude\": 23.73355,\r\n" + //
                        "    \"FoodCategory\": \"fast food\",\r\n" + //
                        "    \"Stars\": 3,\r\n" + //
                        "   \"NoOfVotes\": 1,\r\n" + //
                        "    \"StoreLogo\": \"src/main/resources/kfc_logo.png\",\r\n" + //
                        "   \"Products\": [\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"hot bucket\",\r\n" + //
                        "   \"ProductType\": \"wings\",\r\n" + //
                        "   \"Available Amount\": 4000,\r\n" + //
                        "    \"Price\": 10.85\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"kentucky buckets\",\r\n" + //
                        "   \"ProductType\": \"chicken\",\r\n" + //
                        "   \"Available Amount\": 1050,\r\n" + //
                        "    \"Price\": 10.85\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"fried potatoes\",\r\n" + //
                        "   \"ProductType\": \"potatoes\",\r\n" + //
                        "   \"Available Amount\": 6000,\r\n" + //
                        "    \"Price\": 4.0\r\n" + //
                        "   }\r\n" + //
                        "    ]\r\n" + //
                        "}"),
        new JSONObject("{\r\n" + //
                        "    \"StoreName\": \"L'Artigiano\",\r\n" + //
                        "    \"Latitude\": 37.96825,\r\n" + //
                        "    \"Longitude\": 23.73158,\r\n" + //
                        "    \"FoodCategory\": \"pizzeria\",\r\n" + //
                        "    \"Stars\": 4,\r\n" + //
                        "   \"NoOfVotes\": 506,\r\n" + //
                        "    \"StoreLogo\": \"src/main/resources/lartigiano_logo.png\",\r\n" + //
                        "   \"Products\": [\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"margarita\",\r\n" + //
                        "   \"ProductType\": \"pizza\",\r\n" + //
                        "   \"Available Amount\": 5000,\r\n" + //
                        "    \"Price\": 9.2\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"special\",\r\n" + //
                        "   \"ProductType\": \"pizza\",\r\n" + //
                        "   \"Available Amount\": 1000,\r\n" + //
                        "    \"Price\": 10.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"gold pizza\",\r\n" + //
                        "   \"ProductType\": \"pizza\",\r\n" + //
                        "   \"Available Amount\": 100,\r\n" + //
                        "    \"Price\": 11.0\r\n" + //
                        "   }\r\n" + //
                        "    ]\r\n" + //
                        "}\r\n" + //
                        "   "),
        new JSONObject("{\r\n" + // 
                        "    \"StoreName\": \"McDonalds\",\r\n" + //
                        "    \"Latitude\": 37.97598,\r\n" + //
                        "    \"Longitude\": 23.73376,\r\n" + //
                        "    \"FoodCategory\": \"fast food\",\r\n" + //
                        "    \"Stars\": 2,\r\n" + //
                        "   \"NoOfVotes\": 11987,\r\n" + //
                        "    \"StoreLogo\": \"src/main/resources/mcdonalds_logo.png\",\r\n" + //
                        "   \"Products\": [\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"cheeseburger\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 5000,\r\n" + //
                        "    \"Price\": 1.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"hamburger\",\r\n" + //
                        "   \"ProductType\": \"burger\",\r\n" + //
                        "   \"Available Amount\": 1000,\r\n" + //
                        "    \"Price\": 2.0\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"chef's Salad\",\r\n" + //
                        "   \"ProductType\": \"salad\",\r\n" + //
                        "   \"Available Amount\": 100,\r\n" + //
                        "    \"Price\": 5.0\r\n" + //
                        "   }\r\n" + //
                        "    ]\r\n" + //
                        "}\r\n" + //
                        "   "),
        new JSONObject("{\r\n" + //
                        "    \"StoreName\": \"Pizza Fan\",\r\n" + //
                        "    \"Latitude\": 37.99871,\r\n" + //
                        "    \"Longitude\": 23.73936,\r\n" + //
                        "    \"FoodCategory\": \"pizzeria\",\r\n" + //
                        "    \"Stars\": 3,\r\n" + //
                        "   \"NoOfVotes\": 413,\r\n" + //
                        "    \"StoreLogo\": \"src/main/resources/pizzafun.json\",\r\n" + //
                        "   \"Products\": [\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"margarita\",\r\n" + //
                        "   \"ProductType\": \"pizza\",\r\n" + //
                        "   \"Available Amount\": 3000,\r\n" + //
                        "    \"Price\": 10.1\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"special\",\r\n" + //
                        "   \"ProductType\": \"pizza\",\r\n" + //
                        "   \"Available Amount\": 400,\r\n" + //
                        "    \"Price\": 10.2\r\n" + //
                        "   },\r\n" + //
                        "   {\r\n" + //
                        "    \"ProductName\": \"chef's Salad\",\r\n" + //
                        "   \"ProductType\": \"salad\",\r\n" + //
                        "   \"Available Amount\": 3000,\r\n" + //
                        "    \"Price\": 4.6\r\n" + //
                        "   }\r\n" + //
                        "    ]\r\n" + //
                        "}")
    };
}
