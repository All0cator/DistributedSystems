package com.example.efood_customer.Primitives.Payloads;

import java.io.Serializable;

import com.example.efood_customer.Primitives.HostData;

public class ResultPayload implements Serializable {
    public HostData userHostData;
    public String result;
}
