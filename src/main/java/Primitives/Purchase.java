package Primitives;

import java.io.Serializable;
import java.util.ArrayList;

public class Purchase implements Serializable {
    public String storeName;
    public ArrayList<String> productNames;
    public ArrayList<Integer> amounts; 
}
