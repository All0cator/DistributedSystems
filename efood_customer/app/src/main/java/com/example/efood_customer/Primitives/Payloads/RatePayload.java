package com.example.efood_customer.Primitives.Payloads;

import java.io.Serializable;

import com.example.efood_customer.Primitives.HostData;

public class RatePayload implements Serializable {
    public String storeName;
    public int noOfStars;
    public HostData customerHostData;
}
