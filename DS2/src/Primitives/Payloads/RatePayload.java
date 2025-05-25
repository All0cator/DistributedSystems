package Primitives.Payloads;

import java.io.Serializable;

import Primitives.HostData;

public class RatePayload implements Serializable {
    public String storeName;
    public int noOfStars;
    public HostData customerHostData;
}
