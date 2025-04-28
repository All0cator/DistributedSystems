package Nodes;

import java.util.Scanner;

import Actions.ActionsForManagerApp;
import Primitives.HostData;
import Primitives.Payloads.RegistrationPayload;

public class ManagerApp extends Node {
    private HostData masterHostData;

    public static void main(String[] args) {
        if(args.length != 4) return;

        new ManagerApp(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3])).start();

        loop: 
        while(true){// couldnt figure out where to put this snippet, for now
            System.out.println("Welcome, Manager!");
            System.out.println("1. Add Store");
            System.out.println("2. Add Available Products");
            System.out.println("3. Remove Available Products");
            System.out.println("4. Add New Products");
            System.out.println("5. Remove Old Products");
            System.out.println("6. Print Total Sales per Product");
            System.out.println("7. Exit");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Code to add a new employee
                    break;
                case 2:
                    // Code to view all employees
                    break;
                case 3:
                    // Code to update an employee's information
                    break;
                case 4:
                    // Code to delete an employee
                    break;
                case 5:
                    // Code to delete an employee
                    break;
                case 6:
                    // Code to delete an employee
                    break;
                case 7:
                    System.out.println("Exiting the application.");
                    break loop;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public ManagerApp(String hostIP, int port, String masterHostIP, int masterPort) {
        super(hostIP, port);
        this.actions = new ActionsForManagerApp();
        this.masterHostData = new HostData(masterHostIP, masterPort);
    }

    @Override
    public void Start() {
        this.actions.GetTotalCount(masterHostData, hostData);
    }
}
