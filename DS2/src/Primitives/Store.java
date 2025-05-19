package Primitives;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Store implements Serializable {
    private String name;
    private double latitude;
    private double longitude;
    private String foodCategory;
    private float stars;
    private int noOfVotes;
    private String storeLogo;
    private HashMap<String, Product> nameToProduct;

    public Store(JSONObject json) {
        this.name = json.getString("StoreName");
        this.latitude = json.getDouble("Latitude");
        this.longitude = json.getDouble("Longitude");
        this.foodCategory = json.getString("FoodCategory");
        this.stars = json.getFloat("Stars");
        this.noOfVotes = json.getInt("NoOfVotes");
        this.storeLogo = json.getString("StoreLogo");
        
        JSONArray productJSONs = json.getJSONArray("Products");
        
        this.nameToProduct = new HashMap<String, Product>();

        for(int i = 0; i < productJSONs.length(); ++i) {
            JSONObject productJSON = productJSONs.getJSONObject(i);

            Product product = new Product(productJSON.getString("ProductName"), 
            productJSON.getString("ProductType"), 
            productJSON.getInt("Available Amount"), 
            productJSON.getFloat("Price"));

            this.nameToProduct.put(product.GetName(), product);
        }
    }

    // Copy constructor
    public Store(Store other) {
        this.name = other.GetName();
        this.latitude = other.GetLatitude();
        this.longitude = other.GetLongitude();
        this.foodCategory = other.GetFoodCategory();
        this.stars = other.GetStars();
        this.noOfVotes = other.GetNoOfVotes();
        this.storeLogo = other.GetStoreLogo();
        this.nameToProduct = new HashMap<String, Product>();
        other.CopyNameToProduct(this.nameToProduct); // make deep copy of hashmap
    }

    public void CopyNameToProduct(HashMap<String, Product> map) {
        for(Map.Entry<String, Product> entry : this.nameToProduct.entrySet()) {
            // invoke copy constructors for Product
            map.put(entry.getKey(), new Product(entry.getValue()));
        }
    }

    public double GetDistanceKm(double latitude, double longitude) {
        
        // Calculate distance between store and user using haversine formula
        // https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/

        // convert difference to radians
        double latitudeStore = this.latitude * 3.141519d / 180.0d;
        double latitudeUser = latitude * 3.141519d / 180.0d;

        double dLat = (latitude - this.latitude) * 3.141519d / 180.0d;
        double dLon = (longitude - this.longitude) * 3.141519d / 180.0d;

        return 2.0d * 6371.0d * Math.asin(Math.sqrt(Math.pow(Math.sin(dLat / 2.0d), 2.0d) + Math.pow(Math.sin(dLon / 2.0f), 2.0d) * Math.cos(latitudeStore) * Math.cos(latitudeUser)));
    }

    public String GetName() {
        return this.name;
    }

    public double GetLatitude() {
        return this.latitude;
    }

    public double GetLongitude() {
        return this.longitude;
    }

    public String GetFoodCategory() {
        return this.foodCategory;
    }

    public synchronized float GetStars() {
        return this.stars;
    }

    public synchronized int GetNoOfVotes() {
        return this.noOfVotes;
    }

    public String GetStoreLogo() {
        return this.storeLogo;
    }

    public synchronized ArrayList<Product> GetProducts(boolean isCustomerQuery) {
        ArrayList<Product> products = new ArrayList<Product>();

        for(Product p : this.nameToProduct.values()) {
            if((isCustomerQuery && p.GetCustomerVisibility() == true)
            || !isCustomerQuery) {
                products.add(new Product(p));
            }
        }

        return products;
    }

    public String GetPriceCategory() {
        float totalPrice = 0.0f;

        if(this.nameToProduct.size() == 0) return "$";

        for(Product p : this.nameToProduct.values()) {
            totalPrice = p.GetPrice();
        }

        totalPrice /= this.nameToProduct.size();

        if(totalPrice <= 5.0f) {
            return "$";
        } else if(totalPrice <= 15.0f) {
            return "$$";
        } else {
            return "$$$";
        }
    }
    
    public synchronized boolean MakePurchase(Purchase purchase) {

        if(purchase.productNames == null) return false;
        if(purchase.productNames.length == 0) return false;
        if(purchase.amounts == null) return false;
        if(purchase.amounts.length == 0) return false;

        for(int i = 0; i < purchase.productNames.length; ++i) {
            Product p = this.nameToProduct.get(purchase.productNames[i]);
            boolean isPurchased = p.Purchase(purchase.amounts[i]);
            
            if(!isPurchased) {
                return false;
            }
        }

        return true;
    }

    public synchronized void Restock(String productName, int amount) {

        Product p = this.nameToProduct.get(productName);

        if(p != null) {
            p.Restock(amount);
        }
    }

    public synchronized void AddProduct(Product product) {
        Product p = this.nameToProduct.get(product.GetName());
        
        if(p == null) {
            this.nameToProduct.put(product.GetName(), product);
        }
        else {
            p.ToggleProductCustomerVisibility(true);
        }
    }

    public synchronized void Rate(int noOfStars) {
        this.stars = this.stars +  ((float)noOfStars - this.stars) / ((float)this.noOfVotes + 1.0f);
        this.noOfVotes++;
    }

    public synchronized void RemoveProduct(String productName) {
        Product p = this.nameToProduct.get(productName);

        if(p != null) {
            p.ToggleProductCustomerVisibility(false);
        }
    }

    public synchronized float GetTotalRevenue() {
        float totalRevenue = 0.0f;

        for(Product p : this.nameToProduct.values()) {
            totalRevenue += p.GetTotalRevenue();
        }

        return totalRevenue;
    }

    public synchronized float GetTotalRevenue(String productType) {
        float totalRevenue = 0.0f;

        for(Product p : this.nameToProduct.values()) {
            if(p.GetType().equals(productType)) {
                totalRevenue += p.GetTotalRevenue();
            }
        }

        return totalRevenue;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
