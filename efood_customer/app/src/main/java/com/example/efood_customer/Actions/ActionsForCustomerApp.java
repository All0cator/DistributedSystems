package com.example.efood_customer.Actions;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import com.example.efood_customer.Nodes.CustomerApp;
import com.example.efood_customer.Nodes.Node;
import com.example.efood_customer.Primitives.Message;
import com.example.efood_customer.Primitives.MessageType;
import com.example.efood_customer.Primitives.Store;
import com.example.efood_customer.Primitives.Payloads.FoodCategoriesPayload;
import com.example.efood_customer.Primitives.Payloads.ResultPayload;
import com.example.efood_customer.Primitives.Payloads.StoresPayload;

public class ActionsForCustomerApp extends ActionsForNode {
    private CustomerApp customerApp;

    public ActionsForCustomerApp(Socket connectionSocket, CustomerApp customerApp) {
        super(connectionSocket);
        this.customerApp = customerApp;
    }

    public ActionsForCustomerApp() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForCustomerApp(connectionSocket, (CustomerApp)node);
    }

    @Override
    public void run() {
        try {
            Message message = (Message)iStream.readObject();

            switch (message.type) {
                case RESULT:
                    System.out.println(((ResultPayload)message.payload).result);
                    break;
                case REFRESH_CUSTOMER:
                {
                    FoodCategoriesPayload p = (FoodCategoriesPayload)message.payload;
                    this.customerApp.UpdateFoodCategories(p.foodCategories);

                    this.customerApp.DebugFoodCategories();
                }
                break;
                case FILTER:
                {
                    ArrayList<Store> stores = ((StoresPayload)message.payload).stores;

                    this.customerApp.UpdateStores(stores);

                    this.customerApp.DebugStores();
                }
                break;
                default:
                    break;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
