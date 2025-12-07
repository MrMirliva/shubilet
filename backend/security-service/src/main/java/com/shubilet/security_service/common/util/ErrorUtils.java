package com.shubilet.security_service.common.util;

import com.shubilet.security_service.common.constants.ErrorMessages;
import com.shubilet.security_service.dataTransferObjects.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

public final class ErrorUtils {
    private ErrorUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static MessageDTO isNull(CookieDTO cookie, String fieldName) {
        return new MessageDTO(cookie, fieldName + ErrorMessages.NULL_OR_EMPTY);
    }
    
    public static MessageDTO isInvalidFormat(CookieDTO cookie, String fieldName) {
        return new MessageDTO(cookie, fieldName + ErrorMessages.INVALID_FORMAT);
    }

    public static MessageDTO criticalError(CookieDTO cookie) {
        return new MessageDTO(cookie, ErrorMessages.CRITICAL_ERROR);
    }

    public static MessageDTO userAlreadyLoggedIn(CookieDTO cookie) {
        return new MessageDTO(cookie, ErrorMessages.USER_ALREADY_LOGGED_IN);
    }

    public static MessageDTO notFound(CookieDTO cookie, String entityName) {
        return new MessageDTO(cookie, entityName + ErrorMessages.NOT_FOUND);
    }

    public static MessageDTO isIncorrect(CookieDTO cookie, String fieldName) {
        return new MessageDTO(cookie, fieldName + ErrorMessages.INCORRECT);
    }

    public static MessageDTO sessionNotFound(CookieDTO cookie) {
        return new MessageDTO(cookie, ErrorMessages.SESSION_NOT_FOUND);
    }

    public static MessageDTO sessionExpired(CookieDTO cookie) {
        return new MessageDTO(cookie, ErrorMessages.SESSION_EXPIRED);
    }

    public static MessageDTO invalidSession(CookieDTO cookie) {
        return new MessageDTO(cookie, ErrorMessages.INVALID_SESSION);
    }

    public static CheckMessageDTO invalidSessionForCheckDTO(CookieDTO cookie) {
        return new CheckMessageDTO(cookie, ErrorMessages.INVALID_SESSION, -1);
    }
}
