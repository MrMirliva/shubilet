package com.shubilet.expedition_service.common.enums;

import org.springframework.http.HttpStatus;

public enum BookStatus {
    SUCCESS(true, HttpStatus.OK, "SESSION_VALID"),
    ALREADY_BOOKED(false, HttpStatus.CONFLICT, "SEAT_ALREADY_BOOKED"),
    SEAT_NOT_EXISTS(false, HttpStatus.NOT_FOUND, "SEAT_NOT_EXISTS");

    // Is this session usable in the system?
    private final boolean valid;

    // Which HTTP status should the API respond with?
    private final HttpStatus httpStatus;

    // Message or error key for client / i18n / ErrorMessages
    private final String messageKey;

    BookStatus(boolean valid, HttpStatus httpStatus, String messageKey) {
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
