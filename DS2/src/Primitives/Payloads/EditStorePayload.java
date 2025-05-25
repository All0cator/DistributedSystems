package Primitives.Payloads;

import java.io.Serializable;

public class EditStorePayload implements Serializable {
    public String storeName;
    public String productName;
    public Integer restockValue;
    public Boolean isCustomerVisible;
}
