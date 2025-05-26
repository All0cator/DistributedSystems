package com.example.efood_customer.Primitives.Payloads;

import java.io.Serializable;

import com.example.efood_customer.Primitives.HostData;

public class RegistrationPayload implements Serializable {
    public HostData hostData;
    public boolean isWorkerNode;
}
