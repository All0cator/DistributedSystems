package Primitives;

import java.util.ArrayList;

public class Store {
    private String name;
    private double latitude;
    private double longitude;
    private String foodCategory;
    private double stars;
    private int noOfVotes;
    private String storeLogo;
    private ArrayList<Product> products;
    
    public Store(String name, double latitude, double longitude, String foodCategory, 
        double stars, int noOfVotes, String storeLogo, ArrayList<Product> products)
    {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.foodCategory = foodCategory;
        this.stars = stars;
        this.noOfVotes = noOfVotes;
        this.storeLogo = storeLogo;
        this.products = products;
    }

    public String getName() {
        return name;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getFoodCategory() {
        return foodCategory;
    }
    public double getStars() {
        return stars;
    }
    public int getNoOfVotes() {
        return noOfVotes;
    }
    public String getStoreLogo() {
        return storeLogo;
    }
    public ArrayList<Product> getProducts() {
        return products;
    }
}
