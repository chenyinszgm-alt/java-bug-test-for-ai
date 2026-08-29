package com.example.bugtest.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order {

    /** Order status: paid */
    public static final Integer STATUS_PAID = 1001;
    /** Order status: pending payment */
    public static final Integer STATUS_PENDING = 1002;
    /** Order status: cancelled */
    public static final Integer STATUS_CANCELLED = 1003;
    /** Order status: refunded */
    public static final Integer STATUS_REFUNDED = 1004;

    private Long id;
    private String customerName;
    private Integer status;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private Date createdAt;
    private Date paidAt;

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Date paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id)
                && customerName == order.customerName
                && Objects.equals(status, order.status)
                && Objects.equals(items, order.items)
                && Objects.equals(totalAmount, order.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerName, status, items, totalAmount);
    }
}
