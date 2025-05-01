package Nodes;

import java.util.ArrayList;
import java.util.Scanner;

import Actions.ActionsForCustomerApp;
import Primitives.HostData;
import Primitives.Payloads.RegistrationPayload;
import Primitives.Product;
import Primitives.Store;

public class CustomerApp extends Node {

    private HostData masterHostData;

    public static void main(String[] args) {
        if (args.length != 4) return;

        new CustomerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();

        main_menu:
        //couldn't figure out where to put this snippet, for now
        while (true) {
            System.out.println("Welcome, Customer!");
            System.out.println("0. Exit");
            System.out.println("1. View Stores within 5km Radius");
            System.out.println("2. Filter Stores by Food Categories");
            System.out.println("3. Filter Stores by Stars");
            System.out.println("4. Filter Stores by Price Range ($, $$, $$$)");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            ArrayList<Store> stores = new ArrayList<>();

            switch (choice) {
                case 0:
                    System.out.println("Exiting the application.");
                    break main_menu;
                case 1:
                    // Code to view stores in 5km radius
                    // stores = request stores from master
                    break;
                case 2:
                    // Code to filter stores by food categories
                    // stores = request stores from master
                    break;
                case 3:
                    // Code to filter stores by stars
                    // stores = request stores from master
                    break;
                case 4:
                    // Code to filter stores by price range
                    // stores = request stores from master
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");

            }

            // DEMO
            stores.add(new Store("Store 1", 1d, 1d, "slow food", 4.5d, 10, "logo1", new ArrayList<Product>()));
            stores.add(new Store("Store 2", 1d, 2d, "burgers", 4.1d, 100, "logo2", new ArrayList<Product>()));
            stores.add(new Store("Store 3", 2d, 1d, "pizza", 3.5d, 12, "logo3", new ArrayList<Product>()));

            store_list:
            while (true) {
                System.out.println("Stores");
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
                    break store_list;
                } else {
                    chosenStore = stores.get(choice - 1);
                }

                store_menu:
                while (true) {
                    System.out.println(chosenStore.toString());
                    System.out.println("0. Back");
                    System.out.println("1. Browse Products");
                    System.out.println("2. Leave review");

                    choice = scanner.nextInt();

                    if (choice == 0) {
                        System.out.println("Exiting to store list.");
                        break store_menu;
                    } else if (choice == 1) {
                        ArrayList<Product> products = new ArrayList<>();
                        for (Product product : chosenStore.getProducts()) {
                            if (product.isAvailable()) {
                                products.add(product);
                            }
                        }
                        ArrayList<Product> basketProducts = new ArrayList<>();
                        ArrayList<Integer> basketAmounts = new ArrayList<>();
                        product_list:
                        while (true) {
                            System.out.println(chosenStore.toString());
                            System.out.println("0. Back");
                            System.out.println("1. Basket");
                            for (int i = 0; i < products.size(); i++) {
                                Product product = products.get(i);
                                System.out.println((i + 2) + ". " + product.toString());
                            }

                            choice = scanner.nextInt();

                            if (choice > products.size() + 1 || choice < 0) {
                                System.out.println("Invalid input.");
                                continue product_list;
                            } else if (choice == 0) {
                                System.out.println("Exiting to store menu.");
                                break product_list;
                            } else if (choice == 1) {
                                basket:
                                while (true) {
                                    System.out.println("0. Back to product list");
                                    System.out.println("1. Complete purchase");
                                    System.out.println("2. Remove Product");

                                    for (int i = 0; i < basketProducts.size(); i++) {
                                        System.out.println(basketProducts.get(i).toString());
                                        System.out.println(basketAmounts.get(i).toString());
                                        System.out.println();
                                    }

                                    choice = scanner.nextInt();

                                    if (choice == 0) {
                                        System.out.println("Exiting to product list.");
                                        break basket;
                                    } else if (choice == 1) {
                                        // TODO handle purchase
                                        break basket;
                                    } else if (choice == 2) {
                                        while (true) {
                                            System.out.println("Remove");
                                            System.out.println("0. Done");
                                            for (int i = 0; i < basketProducts.size(); i++) {
                                                Product product = basketProducts.get(i);
                                                System.out.println((i + 1) + ". " + product.toString());
                                            }

                                            choice = scanner.nextInt();

                                            if (choice < 0 || choice > basketProducts.size()) {
                                                System.out.println("Invalid input.");
                                                continue;
                                            } else if (choice == 0) {
                                                System.out.println("Exiting to basket.");
                                                break;
                                            } else {
                                                basketProducts.remove(choice - 1);
                                                basketAmounts.remove(choice - 1);
                                            }
                                        }
                                    } else {
                                        System.out.println("Invalid input.");
                                        continue basket;
                                    }

                                }
                            } else {
                                basketProducts.add(products.get(choice - 2));
                                while (true) {
                                    System.out.println("Input product amount:");
                                    choice = scanner.nextInt();
                                    if (choice > 100 || choice <= 0) {
                                        System.out.println("Invalid input.");
                                        continue;
                                    }
                                    basketAmounts.add(choice);
                                    break;
                                }
                            }
                        }
                    } else if (choice == 2) {
                        // TODO show if has already rated
                        while (true) {
                            System.out.println("Input star amount (Integer 1-5):");
                            choice = scanner.nextInt();
                            if (choice > 5 || choice < 1) {
                                System.out.println("Invalid input.");
                                continue;
                            }
                            // choice = rating, TODO handle here
                            break;
                        }
                    } else {
                        System.out.println("Invalid input.");
                        continue store_menu;
                    }
                }
            }
        }
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