package Nodes;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import Actions.ActionsForCustomerApp;
import Primitives.Filter;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Store;
import Primitives.Payloads.FilterMasterPayload;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.PurchasePayload;
import Primitives.Payloads.RatePayload;
import Primitives.Payloads.RegistrationPayload;
import Primitives.Product;
import Primitives.Purchase;
import Primitives.Store;

public class CustomerApp extends Node {

    private HostData masterHostData;
    private Set<String> foodCategories;
    private ArrayList<Store> stores;

    public static Scanner sc;

    // go to google maps and search for: 37.94448, 23.75171
    private static double latitude = 37.94448d;
    private static double longitude = 23.75171d;
    public static void main(String[] args) {
        if (args.length != 4) return;

        CustomerApp app = new CustomerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]));

        CustomerApp.sc = new Scanner(System.in);

        app.start();

        app.ApplicationLoop();
    }

    public CustomerApp(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForCustomerApp();
        this.masterHostData = new HostData(masterHostIP, masterPort);
        this.foodCategories = new HashSet<String>();
        this.stores = new ArrayList<Store>();
    }

    public synchronized void UpdateStores(ArrayList<Store> stores) {
        this.stores.clear();

        for(int i = 0; i < stores.size(); ++i) {
            this.stores.add(stores.get(i));
        }
    }

    public synchronized void DebugStores() {
        for(Store store : this.stores) {
            System.out.println(store);
        }
    }

    public synchronized void UpdateFoodCategories(ArrayList<String> foodCategories) {
        System.out.println(foodCategories.size());
        this.foodCategories.addAll(foodCategories);
    }

    public synchronized void DebugFoodCategories() {

        System.out.println("Available Food Categories:");

        for(String foodCategory : this.foodCategories) {
            System.out.println(foodCategory);
        }
    }

    public void ApplicationLoop() {
        try {
            while(true){
                System.out.println("Welcome, Customer!");
                System.out.println("1. Refresh");
                System.out.println("2. Search Stores");
                System.out.println("3. New Purchase");
                System.out.println("4. Grade Store");
                System.out.println("5. Exit");
    
                int choice = Integer.parseInt(sc.nextLine());
    
                switch (choice) {
                    case 1: // Refresh food categories
                    {
                        Refresh();
                    }
                    break;
                    case 2: // Filter stores
                    {
                        // Filter stores by food categories, stars, price

                        System.out.println("0) Back");
                        System.out.println("1) Modify Food Category(\"+1\" to add, \"-1\" to remove)");
                        System.out.println("2) Modify Number of Stars Filter(\"+2\" to add, \"-2\" to remove)");
                        System.out.println("3) Modify Price Category Filter(\"+3\" to add, \"-3\" to remove)");
                        System.out.println("4) Submit");

                        Filter filter = new Filter();
                        filter.foodCategories = new HashSet<String>();
                        filter.noOfStars = new HashSet<Integer>();
                        filter.priceCategories = new HashSet<String>();

                        String choice2;
                        String operation;

                        do {

                            do {
                                choice2 = sc.nextLine();
                            } while(!choice2.equals("0")
                            && !choice2.equals("4")
                            && !choice2.equals("+1")
                            && !choice2.equals("-1")
                            && !choice2.equals("+2")
                            && !choice2.equals("-2") 
                            && !choice2.equals("+3")
                            && !choice2.equals("-3"));

                            operation = choice2.substring(0);
                            String sign = "unsigned";
                            
                            if(choice2.length() == 2) {
                                operation = choice2.substring(1);
                                sign = choice2.substring(0, 1);
                            }

                            
                            switch (operation) {
                                case "1":
                                {
                                    // Print food categories
                                    String[] foodCategories =  sign.equals("+") ? GetCopyFoodCategories() : filter.foodCategories.toArray(new String[0]);
                                    if(foodCategories == null) break;
                                    if(foodCategories.length == 0) break;
                                    
                                    for(int i = 0; i < foodCategories.length; ++i) {
                                        System.out.printf("%d) %s\n", i, foodCategories[i]);
                                    }
                            
                                    System.out.print("Choose A food category(-1 to finish selection): ");
                                    
                                    int choice3;
                                    
                                    do {
                                        do {
                                            choice3 = Integer.parseInt(sc.nextLine());
                                        } while(choice3 < -1 || choice3 >= foodCategories.length);
                                        
                                        if(choice3 != -1) {
                                            if(sign.equals("+")) {
                                                if(filter.foodCategories.add(foodCategories[choice3])) {
                                                    System.out.println("Added Food Category: " + foodCategories[choice3]);
                                                }
                                            }
                                            else {
                                                if(filter.foodCategories.remove(foodCategories[choice3])) {
                                                    System.out.println("Removed Food Category: " + foodCategories[choice3]);
                                                }
                                            }
                                        }

                                    } while(choice3 != -1);

                                }    
                                break;
                                case "2":
                                {
                                    
                                    Integer[] stars = null;
                                    Integer a[] = {0, 1, 2, 3, 4 ,5};
                                    if(sign.equals("+")) {
                                        stars = a;
                                    } else {
                                        stars = filter.noOfStars.toArray(new Integer[0]);
                                    }
                                    
                                    if(stars == null) break;
                                    if(stars.length == 0) break;
                                    
                                    for(int i = 0; i < stars.length; ++i) {
                                        System.out.printf("%d) %d\n", i, stars[i]);
                                    }

                                    System.out.print("Choose Number of stars(-1 to finish selection): ");
                                    
                                    int choice3;

                                    do {

                                        do {
                                            choice3 = Integer.parseInt(sc.nextLine());
                                        } while(choice3 < -1 || choice3 > stars.length);
                                        
                                        if(choice3 != -1) {
                                            if(sign.equals("+")) {
                                                if(filter.noOfStars.add(stars[choice3])) {
                                                    System.out.println("Added Number of Stars: " + Integer.toString(stars[choice3]));
                                                }
                                            } else {
                                                if(filter.noOfStars.remove(stars[choice3])) {
                                                    System.out.println("Removed Number of Stars: " + Integer.toString(stars[choice3]));
                                                }
                                            }
                                        }

                                    } while(choice3 != -1);
                                }    
                                break;
                                case "3":
                                {
                                    System.out.println("$ (Average Price <= 5)");
                                    System.out.println("$$ (Average Price <= 15)");
                                    System.out.println("$$$ (Average Price > 15)");
                                    
                                    String[] b = {"$", "$$", "$$$"};
                                    String[] prices = null;
                                    
                                    if(sign.equals("+")) {
                                        prices = b;
                                    } else {
                                        prices = filter.priceCategories.toArray(new String[0]);
                                    }
                                    
                                    if(prices == null) break;
                                    if(prices.length == 0) break;
                                    
                                    for(int i = 0; i < prices.length; ++i) {
                                        System.out.printf("%d) %s\n", i, prices[i]);
                                    }
                                    
                                    System.out.println("Choose Price Category(-1 to finish selection): ");

                                    int choice3;

                                    do {
                                        do {
                                            choice3 = Integer.parseInt(sc.nextLine());
                                        } while(choice3 < -1 || choice3 > prices.length);

                                        if(choice3 != -1) {
                                            if(sign.equals("+")) {
                                                if(filter.priceCategories.add(prices[choice3])) {
                                                    System.out.println("Added Price Category: " + prices[choice3]);
                                                } 
                                            } else {
                                                if(filter.priceCategories.remove(prices[choice3])) {
                                                    System.out.println("Removed Price Category: " + prices[choice3]);
                                                }
                                            }
                                        }

                                    } while(choice3 != -1);
                                }
                                break;
                            }
                           
                        } while(!operation.equals("0") && !operation.equals("4"));

                        if(operation.equals("4")) {
                            // Submit to Filter request to Master

                            Message userMessage = new Message();
                            userMessage.type = MessageType.FILTER;
                            FilterMasterPayload pFilter = new FilterMasterPayload();
                            userMessage.payload = pFilter;
                            pFilter.filter = filter;
                            pFilter.customerLatitude = CustomerApp.latitude;
                            pFilter.customerLongitude = CustomerApp.longitude;
                            pFilter.customerHostData = this.hostData;

                            System.out.println(filter);

                            Socket masterConnection = new Socket(this.masterHostData.GetHostIP(), this.masterHostData.GetPort());
                            ObjectOutputStream oStream = new ObjectOutputStream(masterConnection.getOutputStream());

                            oStream.writeObject(userMessage);
                            oStream.flush();
                        } 
                    }    
                    break;
                    case 3: // Make a Purchase
                    {
                        // Choose a store
                        Store[] storesCopy = GetCopyStores();

                        if(storesCopy == null) break;
                        if(storesCopy.length == 0) break;

                        for(int i = 0; i < storesCopy.length; ++i) {
                            System.out.printf("%d) %s\n", i, storesCopy[i]);
                        }
                
                        System.out.println("Choose A Store to make a purchase from(-1 to cancel -2 to confirm): ");

                        int choice1;

                        do {
                            choice1 = Integer.parseInt(sc.nextLine());
                        } while(choice1 < -1 || choice1 >= storesCopy.length);

                        if(choice1 == -1) break;

                        Store chosenStore = storesCopy[choice1];
                        
                        ArrayList<Product> products = chosenStore.GetProducts(true);

                        ArrayList<String> chosenProducts = new ArrayList<String>();
                        ArrayList<Integer> amounts = new ArrayList<Integer>();

                        System.out.println("Choose A Product(-1 to cancel product):");

                        for(int i = 0; i < products.size(); ++i) {
                            System.out.printf("%d) Name: %s, Type: %s Price: %.2f\n", i, products.get(i).GetName(),
                            products.get(i).GetType(), products.get(i).GetPrice());
                        }

                        // choose a list of products along with an amount
                        int choice2 = -1;
                        int choice3 = -1;

                        do {
                                
                            do {
                                choice2 = Integer.parseInt(sc.nextLine());
                            } while(choice2 < -2 || choice2 > products.size());
                            
                            if(choice2 > -1) {
                                do {
                                    choice3 = Integer.parseInt(sc.nextLine());
                                } while(choice3 < -1 || choice3 == 0);

                                if(choice3 != -1) {
                                    chosenProducts.add(products.get(choice2).GetName());
                                    amounts.add(choice3);
                                    System.out.printf("Added Product: %s Amount: %d to cart\n", products.get(choice2).GetName(), choice3);
                                }
                            }
                                
                        } while(choice2 != -1 && choice2 != -2);

                        if(choice2 == -2) {
                            Purchase purchase = new Purchase();
                            purchase.productNames = chosenProducts.toArray(new String[0]);
                            purchase.amounts = amounts.toArray(new Integer[0]);
                            purchase.storeName = chosenStore.GetName();

                            System.out.println(purchase);

                            // Send it to master

                            Message purchaseMessage = new Message();
                            purchaseMessage.type = MessageType.PURCHASE;
                            PurchasePayload pPurchase = new PurchasePayload();
                            purchaseMessage.payload = pPurchase;
                            
                            pPurchase.purchase = purchase;
                            pPurchase.userHostData = this.hostData;

                            Socket masterConnection = new Socket(this.masterHostData.GetHostIP(), this.masterHostData.GetPort());
                            ObjectOutputStream oStream = new ObjectOutputStream(masterConnection.getOutputStream());

                            oStream.writeObject(purchaseMessage);
                            oStream.flush();
                        }

                    }    
                    break;
                    case 4: // Grade a Store
                    {

                        Store[] storesCopy = GetCopyStores();

                        if(storesCopy == null) break;
                        if(storesCopy.length == 0) break;

                        for(int i = 0; i < storesCopy.length; ++i) {
                            System.out.printf("%d) %s\n", i, storesCopy[i]);
                        }
                
                        System.out.print("Choose A Store to rate(-1 to finish selection): ");

                        
                        int choice1;
                        
                        do {
                            do {
                                choice1 = Integer.parseInt(sc.nextLine());
                            } while(choice1 < -1 || choice1 >= storesCopy.length);
                            
                            if(choice1 != -1) {

                                System.out.print("Choose A Rating(-1 to cancel): 0) 0\n 1) 1\n 2) 2\n 3) 3\n 4) 4\n 5) 5\n");
                                int choice2;
                                
                                do {
                                    choice2 = Integer.parseInt(sc.nextLine());
                                } while(choice2 < -1 || choice2 > 5);
                                
                                if(choice2 == -1) continue;
                                
                                RateStore(storesCopy[choice1], choice2);
                            }
                        } while(choice1 != -1);
                    }
                    break;
                    case 5:
                    {
                        System.out.println("Exiting the application.");
                        return;
                    }
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void RateStore(Store store, int noOfStars) throws IOException {
        Socket masterConnection = new Socket(this.masterHostData.GetHostIP(), this.masterHostData.GetPort());

        ObjectOutputStream oStream = new ObjectOutputStream(masterConnection.getOutputStream());

        Message rateMessage = new Message();
        rateMessage.type = MessageType.RATE;
        RatePayload pRate = new RatePayload();
        rateMessage.payload = pRate;

        pRate.customerHostData = this.hostData;
        pRate.storeName = store.GetName();
        pRate.noOfStars = noOfStars;

        oStream.writeObject(rateMessage);
        oStream.flush();
    }

    public synchronized String[] GetCopyFoodCategories() {
        if(this.foodCategories.size() == 0) return null;

        String foodCategories[] = this.foodCategories.toArray(new String[0]);

        return foodCategories;
    }

    public synchronized Store[] GetCopyStores() {
        if(this.stores.size() == 0) return null;

        Store stores[] = new Store[this.stores.size()];
        
        for(int i = 0; i < this.stores.size(); ++i) {
            stores[i] = new Store(this.stores.get(i));
        }

        return stores;
    }

    public void Refresh() throws UnknownHostException, IOException {
        Message userMessage = new Message();

        userMessage.type = MessageType.REFRESH;
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