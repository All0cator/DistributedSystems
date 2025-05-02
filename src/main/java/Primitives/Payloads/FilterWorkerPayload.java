package Primitives.Payloads;

import java.io.Serializable;

import Primitives.Filter;

public class FilterWorkerPayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public double customerLatitude;
    public double customerLongitude;
    public Filter filter;
}
