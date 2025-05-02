package Primitives;

import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private String type;
    private int availableAmount;
    private float price;
    private boolean isCustomerVisible;
    private float totalRevenue;

    public Product(String name, String type, int availableAmount,
    float price) {
        this.name = name;
        this.type = type;
        this.availableAmount = availableAmount;
        this.price = price;
        this.isCustomerVisible = true;
        this.totalRevenue = 0.0f;
    }

    // Copy constructor
    public Product(Product other) {
        this.name = other.GetName();
        this.type = other.GetType();
        this.availableAmount = other.GetAvailableAmount();
        this.price = other.GetPrice();
        this.isCustomerVisible = other.GetCustomerVisibility();
        this.totalRevenue = other.GetTotalRevenue();
    }

    public int GetAvailableAmount() {
        return this.availableAmount;
    }

    public float GetPrice() {
        return this.price;
    }

    public synchronized boolean Purchase(int amount) {
        if(amount <= this.availableAmount) {
            this.availableAmount -= amount;
            this.totalRevenue += amount * this.price;
            return true;
        }

        return false;
    }

    public synchronized void Restock(int amount) {
        this.availableAmount += amount;
    }

    public String GetType() {
        return this.type;
    }

    public synchronized float GetTotalRevenue() {
        return this.totalRevenue;
    }

    public synchronized void ToggleProductCustomerVisibility(boolean 
    isVisible) {
        this.isCustomerVisible = isVisible;
    }

    public synchronized boolean GetCustomerVisibility() {
        return this.isCustomerVisible;
    }

    public String GetName() {
        return this.name;
    }
}
