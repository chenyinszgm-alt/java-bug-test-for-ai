package com.example.bugtest.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryService {

    private final Map<String, Integer> stock = new HashMap<>();

    public InventoryService() {
        stock.put("SKU-001", 100);
        stock.put("SKU-002", 50);
        stock.put("SKU-003", 200);
    }

    /**
     * Deduct stock for the given sku. Returns false if stock is insufficient.
     */
    public boolean deduct(String sku, int quantity) {
        Integer current = stock.get(sku);
        if (current == null || current < quantity) {
            return false;
        }
        stock.put(sku, current - quantity);
        return true;
    }

    public void increase(String sku, int quantity) {
        Integer current = stock.get(sku);
        if (current == null) {
            stock.put(sku, quantity);
        } else {
            stock.put(sku, current + quantity);
        }
    }

    public int getStock(String sku) {
        Integer value = stock.get(sku);
        return value == null ? 0 : value;
    }
}
