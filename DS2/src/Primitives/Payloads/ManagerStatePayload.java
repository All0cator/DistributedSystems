package Primitives.Payloads;

import java.io.Serializable;
import java.util.Set;

public class ManagerStatePayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public Set<String> foodCategories;
    public Set<String> productTypes;
    public Set<String> storeNames;
}
