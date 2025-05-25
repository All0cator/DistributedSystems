package Primitives;

import java.io.Serializable;
import java.util.ArrayList;

public class Purchase implements Serializable {
    public String storeName;
    public String[] productNames;
    public Integer[] amounts;

    @Override
    public String toString() {
        String res = "";

        res += "Purchase\n";
        res += "Store Name: " + storeName + "\n";

        res += "Products: \n";
        if(productNames != null) {
            for(int i = 0; i < productNames.length; ++i) {
                res += productNames[i] + "\n";
            }
        }

        res += "Amounts: \n";
        if(amounts != null) {
            for(int i = 0; i < amounts.length; ++i) {
                res += Integer.toString(amounts[i]) + "\n";
            }
        }

        return res;
    }
}
