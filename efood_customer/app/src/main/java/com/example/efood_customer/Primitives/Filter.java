package com.example.efood_customer.Primitives;

import java.io.Serializable;
import java.util.Set;

public class Filter implements Serializable {
    public Set<String> foodCategories;
    public Set<Integer> noOfStars;
    public Set<String> priceCategories;

    @Override
    public String toString() {
        String result = "Filter\n";

        result += "Food Categories:\n";

        for(String foodCategory : foodCategories) {
            result += foodCategory + "\n";
        }

        result += "NoOfStars:\n";

        for(Integer noOfStar : noOfStars) {
            result += Integer.toString(noOfStar) + "\n";
        }

        result += "Price Categories:\n";

        for(String priceCategory : priceCategories) {
            result += priceCategory + "\n";
        }

        return result;
    }
}
