package com.shubilet.security_service.common.util;

import com.shubilet.security_service.common.constants.ValidationPatterns;

public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean matches(String value, String regex) {
        return value != null && value.matches(regex);
    }

    public static boolean isValidEmail(String email) {
        return matches(email, ValidationPatterns.EMAIL_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return matches(password, ValidationPatterns.PASSWORD_REGEX);
    }

    /**
     * Validates the session key based on regex and digit sum rules.
     * @param sessionKey The session key to validate.
     * @return True if valid, false otherwise.
     */
    public static boolean isValidSessionKey(String sessionKey) {
        // basic regex validation
        if (!matches(sessionKey, ValidationPatterns.SESSION_KEY_REGEX)) return false;

        // validation digits
        int d1 = sessionKey.charAt(0) - '0';
        int d5 = sessionKey.charAt(5) - '0';
        int d17 = sessionKey.charAt(20) - '0';

        return d1 + d5 + d17 == 15;
    }
}
