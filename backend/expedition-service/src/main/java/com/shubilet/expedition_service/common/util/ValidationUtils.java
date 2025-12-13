package com.shubilet.expedition_service.common.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import com.shubilet.expedition_service.common.constants.ValidationPatterns;

public final class ValidationUtils {
    
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isValidBigDouble(double value) {
        if(value < 0) {
            return false;
        }

        String valueStr = Double.toString(value);
        String[] parts = valueStr.split("\\.");
        if(parts.length == 2) {
            String integerPart = parts[0];
            String fractionalPart = parts[1];
            if(integerPart.length() > 10 || fractionalPart.length() > 2) {
                return false;
            }
        } else if(parts.length == 1) {
            String integerPart = parts[0];
            if(integerPart.length() > 10) {
                return false;
            }
        } else {
            return false;
        }

        return true;
    }

    public static boolean isValidDate(String date) {
        return date != null && date.matches(ValidationPatterns.DATE_PATTERN);
    }

    public static boolean isDateNotInPast(String date, Instant referenceInstant) {
        try {
            LocalDate inputDate = LocalDate.parse(date);
            LocalDate referenceDate = referenceInstant.atZone(ZoneId.systemDefault()).toLocalDate();
            return !inputDate.isBefore(referenceDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
