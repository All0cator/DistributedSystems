import java.util.Scanner;
import java.io.FileReader;

public class ManagerConsoleApp {
    
    public static void main(String[] args) {
        loop: 
        while(true){
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
}
