package Primitives.Payloads;

import java.io.Serializable;

public class ReduceTotalCountPayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public float[] inCounters;
}
