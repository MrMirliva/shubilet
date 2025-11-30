package com.shubilet.security_service.common.enums;

import org.springframework.http.HttpStatus;

/**

    Domain: Session

    Represents the possible lifecycle and validity states of a user session within the
    authentication subsystem. Each enum constant encapsulates whether the session is usable,
    the corresponding HTTP status code that should be returned by the API, and a message key
    used for localization or standardized error responses. This enum provides a clear and
    maintainable mapping between backend session evaluation and API-facing behavior.

    <p>

        Technologies:

        <ul>
            <li>Spring Web {@code HttpStatus} for standardized HTTP response codes</li>
        </ul>

    </p>

    @author Abdullah (Mirliva) GÜNDÜZ — https://github.com/MrMilriva

    @version 1.0
*/
public enum SessionStatus {

    VALID(true, HttpStatus.OK, "SESSION_VALID"),
    NOT_VERIFIED(true, HttpStatus.UNAUTHORIZED, "SESSION_NOT_VERIFIED"),
    EXPIRED(false, HttpStatus.UNAUTHORIZED, "SESSION_EXPIRED"),
    NOT_FOUND(false, HttpStatus.UNAUTHORIZED, "SESSION_NOT_FOUND");

    // Is this session usable in the system?
    private final boolean valid;

    // Which HTTP status should the API respond with?
    private final HttpStatus httpStatus;

    // Message or error key for client / i18n / ErrorMessages
    private final String messageKey;

    SessionStatus(boolean valid, HttpStatus httpStatus, String messageKey) {
        this.valid = valid;
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }

    public boolean isValid() {
        return valid;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessageKey() {
        return messageKey;
    }

}
