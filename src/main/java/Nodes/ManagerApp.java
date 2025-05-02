package Nodes;

import Actions.ActionsForManagerApp;
import Primitives.HostData;
import Primitives.Product;
import Primitives.Store;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class ManagerApp extends Node {
    private final HostData masterHostData;

    public ManagerApp(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForManagerApp();
        this.masterHostData = new HostData(masterHostIP, masterPort);
    }

    public static void main(String[] args) {
        if (args.length != 4) return;

        new ManagerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();


        main_menu:
        while (true) {// couldnt figure out where to put this snippet, for now
            System.out.println("Welcome, Manager!");
            System.out.println("0. Exit");
            System.out.println("1. Add Store");
            System.out.println("2. Edit Products");
            System.out.println("3. Print Total Sales per Product Category");
            System.out.println("4. Print Total Sales per Store Category");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            ArrayList<Store> stores;

            // TESTING DATA, TODO ask master for stores
            stores = new ArrayList<>();
            stores.add(new Store("Store 1", 1d, 1d, "slow food", 4.5d, 10, "logo1", new ArrayList<Product>()));
            stores.add(new Store("Store 2", 1d, 2d, "burgers", 4.1d, 100, "logo2", new ArrayList<Product>()));
            stores.add(new Store("Store 3", 2d, 1d, "pizza", 3.5d, 12, "logo3", new ArrayList<Product>()));


            switch (choice) {
                case 0:
                    System.out.println("Exiting the application.");
                    break main_menu;
                case 1: //we supose that the json file has the proper format
                    try {//in case the file is not found or not a json file
                        // Code to add a new store
                        System.out.println("Enter the json file (or the path to it):");
                        String input = scanner.nextLine();
                        String jsonFile = new String(Files.readAllBytes(Paths.get(input)));

                        JSONObject obj = new JSONObject(jsonFile);
                        JSONArray products = obj.getJSONArray("Products");

                        ArrayList<Product> productList = new ArrayList<>();
                        for (Object p : products) {//creating the product list
                            JSONObject product = (JSONObject) p;

                            String name = (String) product.get("ProductName");
                            String type = (String) product.get("ProductType");
                            String avbamount = (String) product.get("AvailableAmount");
                            String price = (String) product.get("Price");

                            productList.add(new Product(name, type, Integer.parseInt(avbamount), Double.parseDouble(price)));
                        }

                        //create store, I will handle it later
                        Store store = new Store(obj.getString("StoreName"), obj.getDouble("Latitude"), obj.getDouble("Longitude"), obj.getString("FoodCategory"), obj.getDouble("Stars"), obj.getInt("NoOfVotes"), obj.getString("StoreLogo"), productList);

                    } catch (Exception e) {
                        System.out.println("Error reading the file: " + e.getMessage());
                    }
                    break;
                case 2:
                    // Code to Edit Product Availability / Edit Products

                    store_list:
                    while (true) {
                        System.out.println("0. Back");
                        for (int i = 0; i < stores.size(); i++) {
                            Store store = stores.get(i);
                            System.out.println((i + 1) + ". " + store.toString());
                        }

                        choice = scanner.nextInt();

                        Store chosenStore;

                        if (choice > stores.size() || choice < 0) {
                            System.out.println("Invalid input.");
                            continue store_list;
                        } else if (choice == 0) {
                            System.out.println("Exiting to main menu.");
                            break;
                        } else {
                            chosenStore = stores.get(choice - 1);
                        }

                        ArrayList<Product> products = chosenStore.getProducts();
                        store_menu:
                        while (true) {
                            System.out.println(chosenStore);
                            System.out.println("0. Back");
                            System.out.println("1. Add Product");
                            for (int i = 0; i < products.size(); i++) {
                                Product product = products.get(i);
                                if (!product.isAvailable()) System.out.println("AVAILABLE OFF:");
                                System.out.println((i + 2) + ". " + product.toString());
                            }

                            choice = scanner.nextInt();

                            if (choice > products.size() + 1 || choice < 0) {
                                System.out.println("Invalid input.");
                                continue store_menu;
                            } else if (choice == 0) {
                                System.out.println("Exiting to store list.");
                                break store_menu;
                            } else if (choice == 1) {
                                System.out.println("Input name:");
                                String productName = scanner.nextLine();
                                System.out.println("Input product type:");
                                String productType = scanner.nextLine();
                                int availableAmount;
                                while (true) {
                                    System.out.println("Input available amount (Non negative integer)");
                                    availableAmount = scanner.nextInt();
                                    if (availableAmount < 0) continue;
                                    break;
                                }
                                double price;
                                while (true) {
                                    System.out.println("Input price (Positive double)");
                                    price = scanner.nextDouble();
                                    if (price <= 0) continue;
                                    break;
                                }
                                chosenStore.getProducts().add(new Product(productName, productType, availableAmount, price));
                            } else {
                                Product chosenProduct = products.get(choice - 2);
                                product_menu:
                                while (true) {
                                    if (!chosenProduct.isAvailable()) System.out.println("AVAILABLE OFF:");
                                    System.out.println(chosenProduct.toString());
                                    System.out.println("0. Back");
                                    System.out.println("1. Toggle Availability");
                                    System.out.println("2. Edit Available Amount");

                                    choice = scanner.nextInt();

                                    if (choice == 0) {
                                        System.out.println("Exiting to product list.");
                                        break product_menu;
                                    } else if (choice == 1) {
                                        chosenProduct.setAvailable(!chosenProduct.isAvailable());
                                        break product_menu;
                                    } else if (choice == 2) {
                                        available:
                                        while (true) {
                                            System.out.println(chosenProduct.getAvailableAmount());
                                            System.out.println("Input amount to add, negative amount to remove, 0 to cancel:");

                                            choice = scanner.nextInt();

                                            try {
                                                chosenProduct.editAmount(choice);
                                                break available;
                                            } catch (Exception e) {
                                                System.out.println("Invalid subtraction.");
                                                continue available;
                                            }
                                        }
                                    } else {
                                        System.out.println("Invalid input.");
                                        continue product_menu;
                                    }
                                }
                            }
                        }
                    }
                    break;
                case 3:
                    // Code to Print Total Sales per Product Category
                    System.out.println("Input product category:");
                    String productType = scanner.nextLine();
                    int total0 = 0;
                    for (Store store : stores) {
                        int subTotal = 0;
                        for (Product product : store.getProducts()) {
                            if (product.getProductType().equals(productType)) {
                                //TODO get product sales, add them to subTotal
                            }
                        }
                        System.out.println(store.getName() + ": " + subTotal);
                        total0 += subTotal;
                    }
                    System.out.println("total: " + total0);
                    break;
                case 4:
                    // Code to Print Total Sales per Store Category
                    System.out.println("Input store category:");
                    String storeType = scanner.nextLine();
                    int total1 = 0;
                    for (Store store : stores) {
                        if (store.getFoodCategory().equals(storeType))
                            System.out.println(store.getName() + ": "); //TODO get store sales, add them to total
                    }
                    System.out.println("total: " + total1);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void Start() {
        this.actions.GetTotalCount(masterHostData, hostData);
    }
}
