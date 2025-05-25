package Primitives.Payloads;

import java.io.Serializable;

import Primitives.HostData;

public class GetStorePayload implements Serializable {
    public HostData userHostData;
    public String storeName;
}
