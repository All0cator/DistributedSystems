package Primitives.Payloads;

import java.io.Serializable;

import Primitives.HostData;
import Primitives.Purchase;

public class PurchasePayload implements Serializable {
    public Purchase purchase;
    public HostData userHostData;
}
