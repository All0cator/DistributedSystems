package com.example.efood_customer.Primitives;

import android.os.Handler;
import android.util.Log;

import com.example.efood_customer.Primitives.Payloads.FoodCategoriesPayload;
import com.example.efood_customer.Primitives.Payloads.ResultPayload;
import com.example.efood_customer.Primitives.Payloads.StoresPayload;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class MyRunnable<T> implements Runnable{

    Handler handler;

    ArrayList<T> items;
    private String Host = "localhost";
    private int Port = 8080;

    public MyRunnable(Handler handler, ArrayList<T> items){
        this.handler = handler;
        this.items = items;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(Host,Port);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            ArrayList<T> objects = (ArrayList<T>) ois.readObject();

            for(T o : objects){
                items.add(o);
            }

            Log.d("ARRAYLIST","Items:"+items.size());

            ois.close();

//            try {
//                Message message = (Message)iStream.readObject();
//
//                switch (message.type) {
//                    case RESULT:
//                        System.out.println(((ResultPayload)message.payload).result);
//                        break;
//                    case REFRESH_CUSTOMER:
//                    {
//                        FoodCategoriesPayload p = (FoodCategoriesPayload)message.payload;
//                        this.customerApp.UpdateFoodCategories(p.foodCategories);
//
//                        this.customerApp.DebugFoodCategories();
//                    }
//                    break;
//                    case FILTER:
//                    {
//                        ArrayList<Store> stores = ((StoresPayload)message.payload).stores;
//
//                        this.customerApp.UpdateStores(stores);
//
//                        this.customerApp.DebugStores();
//                    }
//                    break;
//                    default:
//                        break;
//                }
//
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

            socket.close();

            handler.sendEmptyMessage(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
