package com.example.bugtest.service;

import com.example.bugtest.model.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PriceCalculator {

    /**
     * Calculate the line total for an order item.
     */
    public static BigDecimal lineTotal(OrderItem item) {
        if (item.getUnitPrice() == null) {
            throw new IllegalArgumentException("unitPrice is required for sku: " + item.getSku());
        }
        return item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }

    /**
     * Apply a discount rate (e.g. 0.05 = 5%) to the given amount and return
     * the amount payable, rounded to 2 decimal places with HALF_UP.
     */
    public static BigDecimal applyDiscount(BigDecimal amount, double discountRate) {
        // BigDecimal.valueOf converts via the string representation and avoids
        // the binary floating point noise of new BigDecimal(double)
        BigDecimal rate = BigDecimal.valueOf(discountRate);
        return amount.subtract(amount.multiply(rate))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
