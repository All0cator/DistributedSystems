package Primitives.Payloads;

import java.io.Serializable;

import Primitives.HostData;

public class RegistrationPayload implements Serializable {
    public HostData hostData;
    public boolean isWorkerNode;
}
