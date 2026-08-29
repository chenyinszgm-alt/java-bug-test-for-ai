package com.example.bugtest.service;

import com.example.bugtest.model.Order;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
public class ReportService {

    /**
     * Export a single order to a local text file.
     */
    public void exportOrder(Order order, String path) {
        try {
            FileWriter writer = new FileWriter(path);
            writer.write("Order: " + order.getId() + "\n");
            writer.write("Customer: " + order.getCustomerName() + "\n");
            writer.write("Total: " + order.getTotalAmount() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
