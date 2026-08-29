package com.example.bugtest.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Date formatting helper.
 *
 * Uses java.time.DateTimeFormatter which is immutable and thread-safe.
 * (The previous implementation shared a single static SimpleDateFormat,
 * which is not thread-safe and corrupted output under concurrent access.)
 */
public class DateUtils {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String format(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
                .format(FORMATTER);
    }

    public static Date parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        LocalDateTime local = LocalDateTime.parse(text, FORMATTER);
        return Date.from(local.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static java.time.Instant parseToInstant(String text) {
        Date date = parse(text);
        return date == null ? null : date.toInstant();
    }
}
