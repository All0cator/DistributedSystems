package com.example.efood_customer.Primitives.Payloads;

import java.io.Serializable;

public class HostDiscoveryRequestPayload implements Serializable {

    public boolean isWorkerNode; // 2 options either we want to discover reducer's ip and port or worker's ip and port based on index in worker's array
    public int index; // only valid when isWorkerNode = false
}
