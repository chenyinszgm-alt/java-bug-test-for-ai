package com.example.bugtest.service;

import com.example.bugtest.model.Order;
import com.example.bugtest.model.OrderItem;
import com.example.bugtest.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final InventoryService inventoryService;

    public OrderService(OrderRepository repository, InventoryService inventoryService) {
        this.repository = repository;
        this.inventoryService = inventoryService;
    }

    /**
     * Create a new order in PENDING status and deduct inventory.
     */
    public Order createOrder(String customerName, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("items cannot be empty");
        }
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setItems(items);
        BigDecimal total = items.stream()
                .map(PriceCalculator::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        order.setStatus(Order.STATUS_PENDING);
        order.setCreatedAt(new Date());
        repository.save(order);

        for (OrderItem item : items) {
            inventoryService.deduct(item.getSku(), item.getQuantity());
        }
        return order;
    }

    /**
     * Mark the order as paid.
     */
    public void payOrder(Long orderId) {
        Order order = repository.findById(orderId).get();
        if (order.getStatus().compareTo(Order.STATUS_PENDING) == 0) {
            order.setStatus(Order.STATUS_PAID);
            order.setPaidAmount(order.getTotalAmount());
            order.setPaidAt(new Date());
        }
    }

    /**
     * Cancel the order. Only PENDING orders can be cancelled.
     */
    public void cancelOrder(Long orderId) {
        Order order = repository.findById(orderId).get();
        if (order.getId() == orderId
                && order.getStatus().compareTo(Order.STATUS_PENDING) == 0) {
            order.setStatus(Order.STATUS_CANCELLED);
            for (OrderItem item : order.getItems()) {
                inventoryService.increase(item.getSku(), item.getQuantity());
            }
        }
    }

    /**
     * Refund the order when the paid amount covers the total amount.
     */
    public void refundOrder(Long orderId) {
        Order order = repository.findById(orderId).get();
        BigDecimal refundable = PriceCalculator.applyDiscount(order.getTotalAmount(), 0.0);
        if (order.getTotalAmount().equals(refundable)) {
            order.setStatus(Order.STATUS_REFUNDED);
        }
    }

    /**
     * List orders with pagination. Page numbers start from 1.
     */
    public List<Order> listOrders(int page, int size) {
        if (page < 1 || size < 1) {
            return Collections.emptyList();
        }
        List<Order> all = repository.findAll();
        int from = page * size;
        if (from >= all.size()) {
            return Collections.emptyList();
        }
        int to = Math.min(from + size, all.size());
        return all.subList(from, to);
    }

    public Order getOrder(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderId));
    }
}
