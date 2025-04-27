package com.iit.tutorials.taxdepartment;

import javafx.beans.property.*;

public class Transaction {

    private final StringProperty itemCode = new SimpleStringProperty();
    private final DoubleProperty internalPrice = new SimpleDoubleProperty();
    private final DoubleProperty discount = new SimpleDoubleProperty();
    private final DoubleProperty salePrice = new SimpleDoubleProperty();
    private final IntegerProperty quantity = new SimpleIntegerProperty();
    private final IntegerProperty checksum = new SimpleIntegerProperty();
    private final BooleanProperty valid = new SimpleBooleanProperty(true);
    private final DoubleProperty profit = new SimpleDoubleProperty();

    public Transaction(String itemCode, double internalPrice, double discount, double salePrice, int quantity, int checksum) {
        this.itemCode.set(itemCode);
        this.internalPrice.set(internalPrice);
        this.discount.set(discount);
        this.salePrice.set(salePrice);
        this.quantity.set(quantity);
        this.checksum.set(checksum);
        this.valid.set(true); // Default to valid
        this.profit.set(0.0); // Will be calculated later
    }

    public Transaction(String itemCode, double internalPrice, double salePrice, int quantity) {
        this.itemCode.set(itemCode);
        this.internalPrice.set(internalPrice);
        this.salePrice.set(salePrice);
        this.quantity.set(quantity);
    }

    // --- Getters and Setters (JavaFX-friendly) ---

    public String getItemCode() {
        return itemCode.get();
    }
    public void setItemCode(String value) {
        itemCode.set(value);
    }
    public StringProperty itemCodeProperty() {
        return itemCode;
    }

    public double getInternalPrice() {
        return internalPrice.get();
    }
    public void setInternalPrice(double value) {
        internalPrice.set(value);
    }
    public DoubleProperty internalPriceProperty() {
        return internalPrice;
    }

    public double getDiscount() {
        return discount.get();
    }
    public void setDiscount(double value) {
        discount.set(value);
    }
    public DoubleProperty discountProperty() {
        return discount;
    }

    public double getSalePrice() {
        return salePrice.get();
    }
    public void setSalePrice(double value) {
        salePrice.set(value);
    }
    public DoubleProperty salePriceProperty() {
        return salePrice;
    }

    public int getQuantity() {
        return quantity.get();
    }
    public void setQuantity(int value) {
        quantity.set(value);
    }
    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public int getChecksum() {
        return checksum.get();
    }
    public void setChecksum(int value) {
        checksum.set(value);
    }
    public IntegerProperty checksumProperty() {
        return checksum;
    }

    public boolean isValid() {
        return valid.get();
    }
    public void setValid(boolean value) {
        valid.set(value);
    }
    public BooleanProperty validProperty() {
        return valid;
    }

    public double getProfit() {
        return profit.get();
    }
    public void setProfit(double value) {
        profit.set(value);
    }
    public DoubleProperty profitProperty() {
        return profit;
    }

    public String toString() {
        return "ItemCode: " + getItemCode() +
                ", Internal Price: Rs." + String.format("%.2f", getInternalPrice()) +
                ", Discount:" + String.format("%2f", getDiscount())+"%" +
                ", Sale price: Rs." + String.format("%2f", getSalePrice())+
                ", Quantity: " + getQuantity() +
                ", Profit: " + String.format("%.2f", getProfit());
    }
}
