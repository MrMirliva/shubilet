package com.shubilet.security_service.common.constants;

import com.shubilet.security_service.common.regex.SessionKeyRegex;

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