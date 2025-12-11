package com.shubilet.security_service.common.util;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.common.constants.ErrorMessages;
import com.shubilet.security_service.dataTransferObjects.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

public final class ErrorUtils {
    private ConversionType conversionType;

    public ErrorUtils(ConversionType conversionType) {
        this.conversionType = conversionType;

    }

    public <T> ResponseEntity<T> isNull(CookieDTO cookie, String fieldName) {
        String message = fieldName + ErrorMessages.NULL_OR_EMPTY;
        return caster(cookie, message, 400);
    }
    
    public <T> ResponseEntity<T> isInvalidFormat(CookieDTO cookie, String fieldName) {
        String message = fieldName + ErrorMessages.INVALID_FORMAT;
        return caster(cookie, message, 400);
    }

    public <T> ResponseEntity<T> criticalError(CookieDTO cookie) {
        String message = ErrorMessages.CRITICAL_ERROR;
        return caster(cookie, message, 500);
    }

    public <T> ResponseEntity<T> userAlreadyLoggedIn(CookieDTO cookie) {
        String message = ErrorMessages.USER_ALREADY_LOGGED_IN;
        return caster(cookie, message, 400);
    }

    public <T> ResponseEntity<T> notFound(CookieDTO cookie, String entityName) {
        String message = entityName + ErrorMessages.NOT_FOUND;
        return caster(cookie, message, 404);
    }

    public <T> ResponseEntity<T> isIncorrect(CookieDTO cookie, String fieldName) {
        String message = fieldName + ErrorMessages.INCORRECT;
        return caster(cookie, message, 400);
    }

    public <T> ResponseEntity<T> sessionNotFound(CookieDTO cookie) {
        String message = ErrorMessages.SESSION_NOT_FOUND;
        return caster(cookie, message, 404);
    }

    public <T> ResponseEntity<T> sessionExpired(CookieDTO cookie) {
        String message = ErrorMessages.SESSION_EXPIRED;
        return caster(cookie, message, 401);
    }

    public <T> ResponseEntity<T> invalidSession(CookieDTO cookie) {
        String message = ErrorMessages.INVALID_SESSION;
        return caster(cookie, message, 401);
    }

    private <T> ResponseEntity<T> caster(CookieDTO cookie, String errorMessage, int errorCode) {
        Object errorObj = null;

        switch (conversionType) {
            case MESSAGE_DTO:
                errorObj = new MessageDTO(cookie, errorMessage);
                break;
            case CHECK_MESSAGE_DTO:
                errorObj = new CheckMessageDTO(cookie, errorMessage, -1);
                break;
        }

        return ResponseEntity.status(errorCode).body((T) errorObj);
    }

    public enum ConversionType {
        MESSAGE_DTO,
        CHECK_MESSAGE_DTO
    }
}
