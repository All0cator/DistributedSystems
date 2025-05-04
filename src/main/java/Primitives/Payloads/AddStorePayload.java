package Primitives.Payloads;

import java.io.Serializable;

import Primitives.HostData;
import Primitives.Store;

public class AddStorePayload implements Serializable {
    public HostData userHostData;
    public Store store;
}
