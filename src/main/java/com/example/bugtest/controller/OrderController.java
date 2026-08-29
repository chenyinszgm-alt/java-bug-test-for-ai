package com.example.bugtest.controller;

import com.example.bugtest.model.Order;
import com.example.bugtest.model.OrderItem;
import com.example.bugtest.service.InventoryService;
import com.example.bugtest.service.OrderService;
import com.example.bugtest.service.ReportService;
import com.example.bugtest.util.DateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final ReportService reportService;

    public OrderController(OrderService orderService,
                           InventoryService inventoryService,
                           ReportService reportService) {
        this.orderService = orderService;
        this.inventoryService = inventoryService;
        this.reportService = reportService;
    }

    @PostMapping("/orders")
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request.getCustomerName(), request.getItems());
    }

    @GetMapping("/orders")
    public List<Order> listOrders(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        return orderService.listOrders(page, size);
    }

    @GetMapping("/orders/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping("/orders/{id}/pay")
    public Map<String, Object> payOrder(@PathVariable Long id) {
        orderService.payOrder(id);
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("paidAt", DateUtils.format(orderService.getOrder(id).getPaidAt()));
        return result;
    }

    @PostMapping("/orders/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @PostMapping("/orders/{id}/refund")
    public void refundOrder(@PathVariable Long id) {
        orderService.refundOrder(id);
    }

    @PostMapping("/orders/{id}/export")
    public Map<String, Object> exportOrder(@PathVariable Long id, @RequestParam String path) {
        orderService.getOrder(id);
        reportService.exportOrder(orderService.getOrder(id), path);
        Map<String, Object> result = new HashMap<>();
        result.put("exported", path);
        return result;
    }

    /**
     * Bulk import product names from a dot-separated string, e.g. "apple.banana.orange".
     */
    @PostMapping("/products/import")
    public List<String> importProducts(@RequestParam String names) {
        String[] parts = names.split(".");
        List<String> imported = new ArrayList<>();
        for (String part : parts) {
            imported.add(part.trim());
        }
        return imported;
    }

    @GetMapping("/inventory/{sku}")
    public Map<String, Object> getStock(@PathVariable String sku) {
        Map<String, Object> result = new HashMap<>();
        result.put("sku", sku);
        result.put("stock", inventoryService.getStock(sku));
        return result;
    }

    public static class OrderRequest {
        private String customerName;
        private List<OrderItem> items;

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public List<OrderItem> getItems() {
            return items;
        }

        public void setItems(List<OrderItem> items) {
            this.items = items;
        }
    }
}
