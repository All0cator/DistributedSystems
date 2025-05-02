package Primitives.Payloads;

import java.io.Serializable;

import Primitives.Filter;
import Primitives.HostData;

public class FilterMasterPayload implements Serializable {
    public HostData customerHostData;
    public double customerLatitude;
    public double customerLongitude;
    public Filter filter;
}
