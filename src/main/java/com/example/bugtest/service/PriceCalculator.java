package com.example.bugtest.service;

import com.example.bugtest.model.OrderItem;

import java.math.BigDecimal;

public class PriceCalculator {

    private PriceCalculator() {
    }

    /**
     * Calculate the total amount of a single order line.
     */
    public static BigDecimal lineTotal(OrderItem item) {
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    /**
     * Apply a discount rate (e.g. 0.05 means 5% off) to the given amount
     * and return the payable amount rounded to 2 decimal places.
     */
    public static BigDecimal applyDiscount(BigDecimal amount, double discountRate) {
        BigDecimal rate = new BigDecimal(discountRate);
        BigDecimal discount = amount.multiply(rate);
        return amount.subtract(discount).setScale(2);
    }
}
