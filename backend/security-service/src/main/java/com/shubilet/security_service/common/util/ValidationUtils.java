package com.shubilet.security_service.common.util;

import com.shubilet.security_service.common.constants.ValidationPatterns;

/**

    Domain: Validation

    Provides a collection of reusable utility methods for validating user input and structured
    data across the application. This class centralizes common validation logic such as regex
    matching, email and password format verification, and session key integrity checks. By
    encapsulating these operations, it promotes consistency, reduces duplication, and supports
    defensive programming practices throughout the authentication subsystem and related modules.

    <p>

        Technologies:

        <ul>
            <li>Core Java regular expression utilities</li>
            <li>Static validation patterns defined in {@code ValidationPatterns}</li>
        </ul>

    </p>

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
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
