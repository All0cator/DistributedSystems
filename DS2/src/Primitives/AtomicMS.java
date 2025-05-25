package Primitives;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// Manager State
public class AtomicMS {
    private Set<String> foodCategories;
    private Set<String> productTypes;
    private ArrayList<Store> stores; 

    public AtomicMS() {
        this.foodCategories = new HashSet<String>();
        this.productTypes = new HashSet<String>();
        this.stores = new ArrayList<Store>();
    }

    public synchronized void AddFoodCategory(String foodCategory) {
        this.foodCategories.add(foodCategory);
    }

    public synchronized void AddProductType(String productType) {
        this.productTypes.add(productType);
    }

    public synchronized void AddStore(Store store) {
        this.stores.add(store);
    }

    public synchronized void GetFoodCategories(Set<String> foodCategories) {
        foodCategories.addAll(this.foodCategories);
    }

    public synchronized void GetProductTypes(Set<String> productTypes) {
        productTypes.addAll(this.productTypes);
    }

    public synchronized void GetStores(ArrayList<Store> stores) {
        stores.addAll(this.stores);
    }

    public synchronized void SetFoodCategories(Set<String> foodCategories) {
        this.foodCategories = foodCategories;
    }

    public synchronized void SetProductTypes(Set<String> productTypes) {
        this.productTypes = productTypes;
    }

    public synchronized void SetStores(ArrayList<Store> stores) {
        this.stores = stores;
    }
}
