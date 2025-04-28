import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

public class CustomerConsoleApp extends Thread{

    ArrayList<Argument> argumentArrayList = new ArrayList<>();
    public CustomerConsoleApp(int size) {
        for(int i=0; i<size;i++){
            UUID uuid = UUID.randomUUID();
            int value = (int) (Math.random() * 100000);
            System.out.println(uuid+ " "+ value);
            Argument argument = new Argument(uuid.toString(),value);
            argumentArrayList.add(argument);
        }
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket("localhost",4444);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(argumentArrayList);
            out.flush();

            argumentArrayList = (ArrayList<Argument>) in.readObject();

            for(Argument a : argumentArrayList){
                System.out.println(a);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


    public static void main(String[] args) {
        loop: 
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
}

