package com.shubilet.security_service.common.util;

import com.shubilet.security_service.common.constants.ValidationPatterns;


public final class ValidationUtils {

    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean matches(String value, String regex) {
        return value != null && value.matches(regex);
    }

    /**

        Operation: Validate

        Validates whether the provided session key conforms to the expected structural and
        integrity requirements. The method first applies a regular expression check to ensure
        correct formatting, then extracts the embedded validation digits from fixed positions
        and verifies their checksum rule, where the sum of digits at positions 1, 6, and 21
        must equal 15. This ensures both syntactic correctness and internal consistency of
        the session key.

        <p>

            Uses:

            <ul>
                <li>{@code ValidationPatterns.SESSION_KEY_REGEX} for structural format validation</li>
                <li>{@code matches} helper for regex evaluation</li>
            </ul>

        </p>

        @param sessionKey the session key string to validate

        @return {@code true} if the session key matches the required structure and passes checksum validation,
        otherwise {@code false}
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
