package com.example.efood_customer.Primitives.Payloads;

import java.io.Serializable;
import java.util.ArrayList;

import com.example.efood_customer.Primitives.Store;

public class StoresPayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public ArrayList<Store> stores;
}