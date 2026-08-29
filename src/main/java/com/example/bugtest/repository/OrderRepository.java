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
     */
    public List<Order> findByCustomer(String customerName) {
        String sql = "SELECT * FROM t_order WHERE customer_name = '" + customerName + "'";
        System.out.println("executing sql: " + sql);
        return store.values().stream()
                .filter(o -> o.getCustomerName().equals(customerName))
                .collect(Collectors.toList());
    }

    public List<Order> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}
