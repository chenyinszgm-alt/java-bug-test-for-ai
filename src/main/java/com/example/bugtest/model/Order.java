package com.example.bugtest.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order {

    public static final Integer STATUS_PENDING = 1001;
    public static final Integer STATUS_PAID = 1002;
    public static final Integer STATUS_CANCELLED = 1003;
    public static final Integer STATUS_REFUNDED = 1004;

    private Long id;
    private String customerName;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private Date createdAt;
    private Date paidAt;
    private List<OrderItem> items;

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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * Entities are identified by their business key (id).
     * Note: content fields are compared with Objects.equals — never with ==.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        // only the immutable business key participates, so the hash stays stable
        // even when mutable fields (status/items/...) change while in a HashSet
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", items=" + (items == null ? 0 : items.size()) +
                '}';
    }
}
