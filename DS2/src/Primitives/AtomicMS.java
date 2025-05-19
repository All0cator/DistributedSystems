package Primitives;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Manager State
public class AtomicMS {
    private Set<String> foodCategories;
    private Set<String> productTypes;
    private Set<String> storeNames; 

    public AtomicMS() {
        this.foodCategories = new HashSet<String>();
        this.productTypes = new HashSet<String>();
        this.storeNames = new HashSet<String>();
    }

    public synchronized void AddFoodCategory(String foodCategory) {
        this.foodCategories.add(foodCategory);
    }

    public synchronized void AddProductType(String productType) {
        this.productTypes.add(productType);
    }

    public synchronized void AddStoreName(String storeName) {
        this.storeNames.add(storeName);
    }

    public synchronized void GetFoodCategories(Set<String> foodCategories) {
        foodCategories.addAll(this.foodCategories);
    }

    public synchronized void GetProductTypes(Set<String> productTypes) {
        productTypes.addAll(this.productTypes);
    }

    public synchronized void GetStoreNames(Set<String> storeNames) {
        storeNames.addAll(this.storeNames);
    }

    public synchronized void SetFoodCategories(Set<String> foodCategories) {
        this.foodCategories = foodCategories;
    }

    public synchronized void SetProductTypes(Set<String> productTypes) {
        this.productTypes = productTypes;
    }

    public synchronized void SetStoreNames(Set<String> storeNames) {
        this.storeNames = storeNames;
    }
}
