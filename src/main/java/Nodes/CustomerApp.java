package Nodes;

import java.util.Scanner;

import Actions.ActionsForCustomerApp;
import Primitives.HostData;
import Primitives.Payloads.RegistrationPayload;

public class CustomerApp extends Node {

    private HostData masterHostData;
    public static void main(String[] args) {
        if(args.length != 4) return;

        new CustomerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();

        loop: //couldn't figure out where to put this snippet, for now
        while(true){
            System.out.println("Welcome, Customer!");
            System.out.println("1. View Stores within 5km Radius");
            System.out.println("2. Filter Stores by Food Categories");
            System.out.println("3. Filter Stores by Stars");
            System.out.println("4. Filter Stores by Price Range ($, $$, $$$)");
            System.out.println("5. Purchase Products");
            System.out.println("6. Rate Stores (1-5 Stars)");
            System.out.println("7. Exit");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Code to view stores in 5km radius
                    break;
                case 2:
                    // Code to filter stores by food categories
                    break;
                case 3:
                    // Code to filter stores by stars
                    break;
                case 4:
                    // Code to filter stores by price range
                    break;
                case 5:
                    // Code to purchase products
                    break;
                case 6:
                    // Code to rate stores
                    break;
                case 7:
                    System.out.println("Exiting the application.");
                    break loop;
                default:
                    System.out.println("Invalid choice. Please try again.");
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