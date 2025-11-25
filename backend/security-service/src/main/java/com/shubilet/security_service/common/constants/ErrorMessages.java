package com.shubilet.security_service.common.constants;

public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    // General error messages
    public static final String NULL_OR_EMPTY = " cannot be null or empty.";
    public static final String INVALID_FORMAT = " has an invalid format.";
    public static final String NOT_FOUND = " not found.";
    public static final String INCORRECT = " is incorrect.";

    // Session
    public static final String USER_ALREADY_LOGGED_IN = "User already logged in.";
}
