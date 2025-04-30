package Primitives;

public class Product {
    private String productName;
    private String productType;
    private int availableAmount;
    private double price;

    public Product(String productName, String productType, int availableAmount, double price) {
        this.productName = productName;
        this.productType = productType;
        this.availableAmount = availableAmount;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }
    public String getProductType() {
        return productType;
    }
    public int getAvailableAmount() {
        return availableAmount;
    }
    public double getPrice() {
        return price;
    }
    public void removeAmount(int amount) {//for when a user buys a product
        if (amount <= availableAmount) {
            availableAmount -= amount;
        } else {
            System.out.println("Not enough stock available.");
        }
    }
}
