package com.example.bugtest.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
    }

    public static String format(Date date) {
        return FORMAT.format(date);
    }

    public static Date parse(String text) throws ParseException {
        return FORMAT.parse(text);
    }
}
