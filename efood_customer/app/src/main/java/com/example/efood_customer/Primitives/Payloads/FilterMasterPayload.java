package com.example.efood_customer.Primitives.Payloads;

import java.io.Serializable;

import com.example.efood_customer.Primitives.Filter;
import com.example.efood_customer.Primitives.HostData;

public class FilterMasterPayload implements Serializable {
    public HostData customerHostData;
    public double customerLatitude;
    public double customerLongitude;
    public Filter filter;
}
