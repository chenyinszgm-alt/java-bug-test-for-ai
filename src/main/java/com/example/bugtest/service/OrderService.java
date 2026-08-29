package com.example.bugtest.service;

import com.example.bugtest.model.Order;
import com.example.bugtest.model.OrderItem;
import com.example.bugtest.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class OrderService {

    private static final int MAX_PAGE_SIZE = 100;

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
        // Deduct inventory FIRST; fail the whole order if any SKU is insufficient.
        // (previously the deduct result was ignored and the order was saved regardless)
        List<OrderItem> deducted = new ArrayList<>();
        for (OrderItem item : items) {
            if (!inventoryService.deduct(item.getSku(), item.getQuantity())) {
                // roll back what has already been deducted
                for (OrderItem done : deducted) {
                    inventoryService.increase(done.getSku(), done.getQuantity());
                }
                throw new IllegalArgumentException(
                        "insufficient stock for sku: " + item.getSku());
            }
            deducted.add(item);
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        // defensive copy: do not share the mutable list with the caller
        order.setItems(new ArrayList<>(items));
        BigDecimal total = items.stream()
                .map(PriceCalculator::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        order.setStatus(Order.STATUS_PENDING);
        order.setCreatedAt(new Date());
        repository.save(order);
        return order;
    }

    /**
     * Mark the order as paid. Returns the (possibly updated) order.
     */
    public Order payOrder(Long orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus().compareTo(Order.STATUS_PENDING) == 0) {
            order.setStatus(Order.STATUS_PAID);
            order.setPaidAmount(order.getTotalAmount());
            order.setPaidAt(new Date());
        }
        return order;
    }

    /**
     * Cancel the order. Only PENDING orders can be cancelled.
     */
    public void cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus().compareTo(Order.STATUS_PENDING) == 0) {
            order.setStatus(Order.STATUS_CANCELLED);
            for (OrderItem item : order.getItems()) {
                inventoryService.increase(item.getSku(), item.getQuantity());
            }
        }
    }

    /**
     * Refund the order. Only PAID orders whose paid amount covers the total can be refunded.
     */
    public void refundOrder(Long orderId) {
        Order order = getOrder(orderId);
        // state check: only a PAID order can be refunded
        if (order.getStatus().compareTo(Order.STATUS_PAID) != 0) {
            throw new IllegalStateException(
                    "order " + orderId + " is not paid, cannot refund, status: " + order.getStatus());
        }
        BigDecimal paid = order.getPaidAmount() == null ? BigDecimal.ZERO : order.getPaidAmount();
        if (paid.compareTo(order.getTotalAmount()) < 0) {
            throw new IllegalStateException(
                    "order " + orderId + " is not fully paid, paid=" + paid
                            + ", total=" + order.getTotalAmount());
        }
        order.setStatus(Order.STATUS_REFUNDED);
    }

    /**
     * List orders with pagination. Page numbers start from 1.
     */
    public List<Order> listOrders(int page, int size) {
        if (page < 1 || size < 1) {
            return Collections.emptyList();
        }
        if (size > MAX_PAGE_SIZE) {
            size = MAX_PAGE_SIZE;
        }
        List<Order> all = repository.findAll();
        // sort by id for stable pagination across calls
        all.sort(Comparator.comparing(Order::getId));
        // page is 1-based: use long math to avoid int overflow, then clamp
        long from = (long) (page - 1) * size;
        if (from >= all.size()) {
            return Collections.emptyList();
        }
        int fromIdx = (int) from;
        int to = (int) Math.min(from + size, (long) all.size());
        return new ArrayList<>(all.subList(fromIdx, to));
    }

    public Order getOrder(Long orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderId));
    }
}
