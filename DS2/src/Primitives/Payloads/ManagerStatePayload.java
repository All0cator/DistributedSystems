package Primitives.Payloads;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;
import Primitives.Store;

public class ManagerStatePayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public Set<String> foodCategories;
    public Set<String> productTypes;
    public ArrayList<Store> stores;
}
