package Primitives.Payloads;

import java.io.Serializable;

public class WorkerRegistrationPayload implements Serializable {
    public int workerID;
    public String[] jsonStores; // json object is not serializable lol
}
