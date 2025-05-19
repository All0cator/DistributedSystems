package Primitives.Payloads;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

import Primitives.HostData;

public class FoodCategoriesPayload implements Serializable {
    public int mapID;
    public int numWorkers;
    public HostData userHostData;
    public Set<String> foodCategories;
}