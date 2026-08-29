package com.example.bugtest.service;

import com.example.bugtest.model.Order;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ReportService {

    /**
     * All exports are written into this fixed directory.
     * The client cannot choose an arbitrary path (prevents path traversal).
     */
    private static final Path EXPORT_DIR = Paths.get("reports");

    /**
     * Export the order to a file under the fixed export directory.
     * Returns the actual file path written.
     */
    public String exportOrder(Order order) {
        Path file = EXPORT_DIR.resolve("order-" + order.getId() + ".md");
        String content = order.toString();
        try {
            Files.createDirectories(EXPORT_DIR);
            Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Do not swallow: the caller gets a meaningful failure instead of a fake success
            throw new IllegalStateException("failed to export order " + order.getId(), e);
        }
        return file.toString();
    }
}
