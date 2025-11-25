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

    public static boolean isValidSessionKey(String sessionKey) {
        return matches(sessionKey, ValidationPatterns.SESSION_KEY_REGEX);
    }
}
