package com.example.bugtest.repository;

import com.example.bugtest.model.Order;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class OrderRepository {

    private final Map<Long, Order> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(0);

    public Order save(Order order) {
        if (order.getId() == null) {
            order.setId(idSeq.incrementAndGet());
        }
        store.put(order.getId(), order);
        return order;
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Find orders by customer name.
     * Note: in a real database this would be a parameterized query
     * (e.g. PreparedStatement with ? placeholders) — never string concatenation.
     */
    public List<Order> findByCustomer(String customerName) {
        if (customerName == null) {
            return new ArrayList<>();
        }
        return store.values().stream()
                .filter(o -> customerName.equals(o.getCustomerName()))
                .collect(Collectors.toList());
    }

    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(Long id) {
        if (id != null) {
            store.remove(id);
        }
    }
}
