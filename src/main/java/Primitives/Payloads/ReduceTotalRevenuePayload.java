package Primitives.Payloads;

import java.io.Serializable;
import java.util.HashMap;

public class ReduceTotalRevenuePayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public HashMap<String, Float> storeNameToTotalRevenue;
}
