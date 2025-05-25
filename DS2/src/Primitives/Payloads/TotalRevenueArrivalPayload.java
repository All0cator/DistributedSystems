package Primitives.Payloads;

import java.io.Serializable;
import java.util.HashMap;

public class TotalRevenueArrivalPayload implements Serializable {
    public int mapID;
    public HashMap<String, Float> storeNameToTotalRevenue;
}
