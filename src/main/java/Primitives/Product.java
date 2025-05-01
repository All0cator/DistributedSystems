package Primitives;

public class Product {
    private String productName;
    private String productType;
    private int availableAmount;
    private double price;
    private boolean available;

    public Product(String productName, String productType, int availableAmount, double price) {
        this.productName = productName;
        this.productType = productType;
        this.availableAmount = availableAmount;
        this.price = price;
        this.setAvailable(true);
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

    public void editAmount(int amount) throws Exception {
        if (amount + availableAmount < 0) {
            throw new Exception("Not enough stock available.");
        }

        this.availableAmount += amount;
    }

    public String toString() {
        return this.getProductName() + " | " + this.getProductType() + " | " + this.getPrice() + (this.getAvailableAmount() == 0 ? " | UNAVAILABLE" : "");
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
