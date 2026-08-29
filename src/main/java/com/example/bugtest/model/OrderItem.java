package com.example.bugtest.model;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {

    private String sku;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem() {
    }

    public OrderItem(String sku, String productName, int quantity, BigDecimal unitPrice) {
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem item = (OrderItem) o;
        return quantity == item.quantity
                && Objects.equals(sku, item.sku)
                && Objects.equals(productName, item.productName)
                && Objects.equals(unitPrice, item.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku, productName, quantity, unitPrice);
    }
}
