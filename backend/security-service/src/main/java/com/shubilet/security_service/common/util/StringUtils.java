package com.shubilet.security_service.common.util;

public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Checks if the given string is null or contains only whitespace.
     */
    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Null-safe string equality check.
     */
    public static boolean nullSafeEquals(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }

    /**
     * Checks if a string consists only of digits (0-9).
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
