package Nodes;

import Actions.ActionsForManagerApp;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Product;
import Primitives.Store;
import Primitives.Payloads.AddStorePayload;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.StoresPayload;
import Primitives.Payloads.TotalRevenuePayload;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ManagerApp extends Node {
    private final HostData masterHostData;

    public static Scanner sc;

    private Set<String> foodCategories;
    private Set<String> productTypes;
    private ArrayList<Store> stores;

    public ManagerApp(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForManagerApp();
        this.masterHostData = new HostData(masterHostIP, masterPort);
        this.foodCategories = new HashSet<String>();
        this.productTypes = new HashSet<String>();
        this.stores = new ArrayList<Store>();
    }

    // Atomic operation clear and add
    public synchronized void UpdateState(Set<String> foodCategories, Set<String> productTypes, ArrayList<Store> stores) {
        this.foodCategories.clear();
        this.productTypes.clear();
        this.stores.clear();

        this.foodCategories.addAll(foodCategories);
        this.productTypes.addAll(productTypes);
        this.stores.addAll(stores);
    }

    // Atomic operation add
    public synchronized void UpdateStateIncremental(Set<String> foodCategories, Set<String> productTypes, ArrayList<Store> stores) {
        this.foodCategories.addAll(foodCategories);
        this.productTypes.addAll(productTypes);
        this.stores.addAll(stores);
    }

    public synchronized void DebugState() {
        System.out.println("Food Categories: ");
        for(String foodCategory : this.foodCategories) {
            System.out.println(foodCategory);
        }

        System.out.println("Product Types: ");
        for(String productType : this.productTypes) {
            System.out.println(productType);
        }

        System.out.println("Store Names: ");
        for(Store store : this.stores) {
            System.out.println(store.GetName());
        }
    }

    public synchronized String[] GetCopyFoodCategories() {
        if(this.foodCategories.size() == 0) return null;

        String result[] = this.foodCategories.toArray(new String[0]);

        return result;
    }

    public synchronized String[] GetCopyProductTypes() {
        if(this.productTypes.size() == 0) return null;

        String result[] = this.productTypes.toArray(new String[0]);

        return result;
    }

    public synchronized Store[] GetCopyStores() {
        if(this.stores.size() == 0) return null;

        Store result[] = this.stores.toArray(new Store[0]);

        return result;
    }

    public static void main(String[] args) {
        if (args.length != 4) return;

        ManagerApp app = new ManagerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));

        app.start();

        ManagerApp.sc = new Scanner(System.in);

        app.ApplicationLoop();
    }

    public void ApplicationLoop() {
        try {
            while(true){
                System.out.println("Welcome, Manager!");
                System.out.println("1. Refresh");
                System.out.println("2. Add New Store");
                System.out.println("3. Edit Store");
                System.out.println("4. Query Revenue");
                System.out.println("5. Exit");
    
                int choice = Integer.parseInt(sc.nextLine());
    
                switch (choice) {
                    case 1: // Refresh food categories, product types, store names
                    {
                        Refresh();
                    }
                    break;
                    case 2: // Add New Store
                    {

                        JSONObject jsonStore = new JSONObject();

                        System.out.println("Insert New Store Data: ");
                        
                        System.out.print("StoreName: ");
                        String storeName = sc.nextLine();
                        System.out.print("Latitude: ");
                        double latitude = Double.parseDouble(sc.nextLine());
                        System.out.print("Longitude: ");
                        double longitude = Double.parseDouble(sc.nextLine());
                        System.out.print("FoodCategory: ");
                        String foodCategory = sc.nextLine();
                        System.out.print("Stars: ");
                        float stars = Float.parseFloat(sc.nextLine());
                        System.out.print("NoOfVotes: ");
                        int noOfVotes = Integer.parseInt(sc.nextLine());
                        System.out.print("StoreLogo: ");
                        String storeLogo = sc.nextLine();

                        jsonStore.put("StoreName", storeName);
                        jsonStore.put("Latitude", latitude);
                        jsonStore.put("Longitude", longitude);
                        jsonStore.put("FoodCategory", foodCategory);
                        jsonStore.put("Stars", stars);
                        jsonStore.put("NoOfVotes", noOfVotes);
                        jsonStore.put("StoreLogo", storeLogo);
                    
                        System.out.println("Insert Products(-1) Finish, 0) New Product, 1) Remove Last Product): ");

                        int choice1;

                        JSONArray jsonProducts = new JSONArray();

                        do {

                            do {
                                choice1 = Integer.parseInt(sc.nextLine());
                            } while(choice1 < -1 || choice1 > 1);
                            
                            if(choice1 != -1) {
                                if(choice1 == 1) {
                                    if(jsonProducts.length() > 0) {
                                        JSONObject removedProduct = (JSONObject)jsonProducts.remove(jsonProducts.length() - 1);
                                        System.out.printf("Product Removed: %s\n", removedProduct.getString("ProductName"));
                                    }
                                    continue;
                                }

                                System.out.print("ProductName: ");
                                String productName = sc.nextLine();
                                System.out.print("ProductType: ");
                                String productType = sc.nextLine();
                                System.out.print("Available Amount: ");
                                int availableAmount = Integer.parseInt(sc.nextLine());
                                System.out.print("Price: ");
                                float price = Float.parseFloat(sc.nextLine());

                                JSONObject jsonProduct = new JSONObject();
                                jsonProduct.put("ProductName", productName);
                                jsonProduct.put("ProductType", productType);
                                jsonProduct.put("Available Amount", availableAmount);
                                jsonProduct.put("Price", price);

                                jsonProducts.put(jsonProduct);
                                System.out.println("Product Added!");
                            }
                            
                        } while(choice1 != -1);

                        // Add JSONArray to jsonStore
                        jsonStore.put("Products", jsonProducts);

                        // Send it to master -> correct worker
                        // Get back a payload with the new store name, new food category, new product types if it is valid
                        // and add them to ManagerState

                        Message addStoreMessage = new Message();
                        addStoreMessage.type = MessageType.ADD_STORE;
                        AddStorePayload pStore = new AddStorePayload();
                        addStoreMessage.payload = pStore;

                        pStore.userHostData = this.hostData;
                        pStore.store = new Store(jsonStore);

                        this.actions.SendMessageToNode(this.masterHostData, addStoreMessage);

                        /*{
                            "StoreName": "Domino's",
                            "Latitude": 37.99625,
                            "Longitude": 23.73303,
                            "FoodCategory": "pizzeria",
                            "Stars": 4,
                            "NoOfVotes": 444,
                            "StoreLogo": "src/main/resources/dominos_logo.png",
                            "Products": [
                            {
                            "ProductName": "margarita",
                            "ProductType": "pizza",
                            "Available Amount": 5000,
                            "Price": 9.2
                            },
                            {
                            "ProductName": "special",
                            "ProductType": "pizza",
                            "Available Amount": 1000,
                            "Price": 12.0
                            },
                            {
                            "ProductName": "chef's Salad",
                            "ProductType": "salad",
                            "Available Amount": 100,
                            "Price": 5.0
                            }
                            ]
                        }*/
                    }
                    break;
                    case 3: // Edit Store
                    {
                        // Restock
                        // Add/Remove Product
                    }
                    break;
                    case 4: // Query Revenue Statistics
                    {
                        // Total Revenue by Product Type
                        // Total Revenue by Food Category

                        System.out.println("Choose query(-1 to cancel)");
                        System.out.println("0) Total Revenue by Product Type");
                        System.out.println("1) Total Revenue by Food Category");
                        

                        int choice1;

                        do {
                            choice1 = Integer.parseInt(sc.nextLine());
                        } while(choice1 < -1 || choice1 > 1);

                        if(choice1 != -1) {

                            
                            String[] choices = null;
                            
                            if(choice1 == 0) {
                                // Product Type
                                choices = GetCopyProductTypes();
                            }
                            else {
                                // Food Category
                                choices = GetCopyFoodCategories();
                            }
                            
                            if(choices == null) break;
                            if(choices.length == 0) break;
                            
                            System.out.println("Choose an option(-1 to cancel): ");
                            
                            for(int i = 0; i < choices.length; ++i) {
                                System.out.printf("%d) %s\n", i, choices[i]);
                            }

                            int choice2;

                            do {
                                choice2 = Integer.parseInt(sc.nextLine());
                            } while(choice2 < -1 || choice2 >= choices.length);

                            if(choice2 != -1) {

                                String chosenChoice = choices[choice2];
                                
                                Message masterMessage = new Message();
                                masterMessage.type = choice1 == 1 ? MessageType.TOTAL_REVENUE_PER_FOOD_CATEGORY : MessageType.TOTAL_REVENUE_PER_PRODUCT_TYPE;
                                TotalRevenuePayload pTotal = new TotalRevenuePayload();
                                masterMessage.payload = pTotal;
                                
                                pTotal.userHostData = this.hostData;
                                pTotal.type = chosenChoice;

                                this.actions.SendMessageToNode(this.masterHostData, masterMessage);
                            }


                        }
                    }
                    break;
                    case 5: // Exit
                    {
                        System.out.println("Exiting the application.");
                        return;
                    }
                    default:
                        System.out.println("Invalid choice. Please try again.");
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void Refresh() throws UnknownHostException, IOException {
        Message userMessage = new Message();

        userMessage.type = MessageType.REFRESH_MANAGER;
        HostDataPayload pHostData = new HostDataPayload();
        userMessage.payload = pHostData;
        pHostData.hostData = this.hostData;

        // send to master
        Socket masterConnection = new Socket(this.masterHostData.GetHostIP(), this.masterHostData.GetPort());

        ObjectOutputStream oStream = new ObjectOutputStream(masterConnection.getOutputStream());

        oStream.writeObject(userMessage);
        oStream.flush();
    }

    @Override
    public void Start() {
        this.actions.GetTotalCount(masterHostData, hostData);
    }
}
