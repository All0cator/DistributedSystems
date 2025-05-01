import java.util.Scanner;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import Primitives.Product;
import Primitives.Store;

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

                            productList.add(new Product(name, type, Integer.parseInt(avbamount), 
                                                Double.parseDouble(price)));
                        }

                        //create store, i will handle it later
                        Store store = new Store(obj.getString("StoreName"), obj.getDouble("Latitude"), 
                                        obj.getDouble("Longitude"), obj.getString("FoodCategory"), 
                                        obj.getDouble("Stars"), obj.getInt("NoOfVotes"), 
                                        obj.getString("StoreLogo"), productList);
                        
                    } catch (Exception e) {
                        System.out.println("Error reading the file: " + e.getMessage());
                    }
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
