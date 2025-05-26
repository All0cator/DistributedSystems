package com.example.efood_customer.Primitives.Payloads;

import java.io.Serializable;

import com.example.efood_customer.Primitives.HostData;
import com.example.efood_customer.Primitives.Purchase;

public class PurchasePayload implements Serializable {
    public Purchase purchase;
    public HostData userHostData;
}
