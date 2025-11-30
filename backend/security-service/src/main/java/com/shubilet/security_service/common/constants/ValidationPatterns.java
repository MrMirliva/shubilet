package com.shubilet.security_service.common.constants;

import com.shubilet.security_service.common.regex.SessionKeyRegex;

/**

        Domain: Validation
        Provides a centralized collection of regular expression patterns used across the
        application to validate structured input such as emails, passwords, phone numbers,
        and session keys. By consolidating these regex definitions into a single utility
        class, the application ensures consistent validation behavior and avoids hard-coded,
        duplicated patterns throughout the codebase. The class is final and non-instantiable,
        serving strictly as a static holder for validation constants.

        <p>

                Technologies:

                <ul>
                        <li>Core Java regular expression utilities</li>
                        <li>{@code SessionKeyRegex} for dynamically generating the session key pattern</li>
                </ul>

        </p>

        @author Abdullah (Mirliva) GÜNDÜZ — https://github.com/MrMilriva

        @version 1.0
*/
public final class ValidationPatterns {

        private ValidationPatterns() {
                throw new UnsupportedOperationException("Utility class");
        }

        public static final String EMAIL_REGEX = 
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

        public static final String PASSWORD_REGEX = 
                "^(?=.*[A-Za-z])(?=.*\\d).{8,}$";

        public static final String PHONE_REGEX = 
                "^\\+?90[0-9]{10}$";

        public static final String SESSION_KEY_REGEX = 
                SessionKeyRegex.build();
}