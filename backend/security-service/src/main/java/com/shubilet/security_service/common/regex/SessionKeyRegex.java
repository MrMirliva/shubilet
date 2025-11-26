package com.shubilet.security_service.common.regex;

import com.shubilet.security_service.common.constants.AppConstants;

public final class SessionKeyRegex {

    private SessionKeyRegex() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Builds the regex pattern for session keys.
     * @return The regex pattern as a String.
     */
    public static String build() {
        String alphabet = AppConstants.ALPHABET; 

        String cc = "[" + alphabet + "]";

        return "^\\d" + cc + "{3}-" +
               "\\d" + cc + "{3}-" +
               cc + "{4}-" +
               cc + "{4}-" +
               "\\d" + cc + "{3}-" +
               cc + "{4}-" +
               cc + "{4}-" +
               cc + "{4}$";
    }
}
