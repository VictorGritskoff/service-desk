package io.flow.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    public static Instant parseDateRange(String range) {
        if (range == null || range.isBlank()) {
            return null;
        }
        if (range.matches("\\d+d")) { // Match format like "7d", "30d"
            int days = Integer.parseInt(range.replace("d", ""));
            return Instant.now().minus(days, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        }
        throw new IllegalArgumentException("Invalid range format. Use format like '7d' or '30d'.");
    }


    public static Instant truncateToMidnight(Instant instant) {
        return (instant != null) ? instant.truncatedTo(ChronoUnit.DAYS) : null;
    }
}
