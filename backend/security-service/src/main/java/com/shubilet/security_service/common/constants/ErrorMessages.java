package com.shubilet.security_service.common.constants;

/**

    Domain: ErrorHandling

    Defines a centralized collection of immutable error message templates used throughout the
    application to ensure consistent construction of human-readable error responses. These
    messages cover general validation failures as well as session-specific conditions such as
    expiration, missing sessions, and verification issues. As a final utility class, it is
    non-instantiable and serves purely as a static container for shared constant strings.

    <p>

        Technologies:

        <ul>
            <li>Core Java constant definition</li>
        </ul>

    </p>

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("Utility class");
    }

    // General error messages
    public static final String NULL_OR_EMPTY = " cannot be null or empty.";
    public static final String INVALID_FORMAT = " has an invalid format.";
    public static final String NOT_FOUND = " not found.";
    public static final String INCORRECT = " is incorrect.";
    public static final String NOT_VERIFIED = " is not verified.";
    public static final String CRITICAL_ERROR = "A critical error has occurred.";

    // Session
    public static final String USER_ALREADY_LOGGED_IN = "User already logged in.";
    public static final String SESSION_NOT_FOUND = "Session not found.";
    public static final String SESSION_EXPIRED = "Session has expired.";
    public static final String INVALID_SESSION = "Invalid session.";
    public static final String SESSION_NOT_VERIFIED = " is not verified.";
}
