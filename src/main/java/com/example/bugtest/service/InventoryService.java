package com.example.bugtest.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InventoryService {

    /**
     * Stock is shared mutable state accessed by concurrent request threads,
     * so a ConcurrentHashMap is used instead of HashMap.
     */
    private final Map<String, Integer> stock = new ConcurrentHashMap<>();

    public InventoryService() {
        stock.put("SKU-001", 100);
        stock.put("SKU-002", 50);
    }

    /**
     * Atomically deduct stock for the given sku.
     * The check and the update happen inside a single compute() call,
     * so concurrent deductions cannot oversell.
     */
    public boolean deduct(String sku, int quantity) {
        if (sku == null || quantity <= 0) {
            return false;
        }
        boolean[] ok = {false};
        stock.computeIfPresent(sku, (k, current) -> {
            if (current >= quantity) {
                ok[0] = true;
                return current - quantity;
            }
            return current;
        });
        return ok[0];
    }

    /**
     * Atomically increase stock for the given sku.
     */
    public void increase(String sku, int quantity) {
        if (sku == null || quantity <= 0) {
            return;
        }
        stock.merge(sku, quantity, Integer::sum);
    }

    public int getStock(String sku) {
        Integer v = stock.get(sku);
        return v == null ? 0 : v;
    }
}
