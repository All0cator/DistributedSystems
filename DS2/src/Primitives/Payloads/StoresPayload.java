package Primitives.Payloads;

import java.io.Serializable;
import java.util.ArrayList;

import Primitives.Store;

public class StoresPayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public ArrayList<Store> stores;
}