package com.shubilet.security_service.common.util;

import com.shubilet.security_service.common.constants.ErrorMessages;
import com.shubilet.security_service.dataTransferObjects.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

public final class ErrorUtils {
    private ConversionType conversionType;

    public ErrorUtils(ConversionType conversionType) {
        this.conversionType = conversionType;

    }

    public <T> T isNull(CookieDTO cookie, String fieldName) {
        String message = fieldName + ErrorMessages.NULL_OR_EMPTY;
        return caster(cookie, message);
    }
    
    public <T> T isInvalidFormat(CookieDTO cookie, String fieldName) {
        String message = fieldName + ErrorMessages.INVALID_FORMAT;
        return caster(cookie, message);
    }

    public <T> T criticalError(CookieDTO cookie) {
        String message = ErrorMessages.CRITICAL_ERROR;
        return caster(cookie, message);
    }

    public <T> T userAlreadyLoggedIn(CookieDTO cookie) {
        String message = ErrorMessages.USER_ALREADY_LOGGED_IN;
        return caster(cookie, message);
    }

    public <T> T notFound(CookieDTO cookie, String entityName) {
        String message = entityName + ErrorMessages.NOT_FOUND;
        return caster(cookie, message);
    }

    public <T> T isIncorrect(CookieDTO cookie, String fieldName) {
        String message = fieldName + ErrorMessages.INCORRECT;
        return caster(cookie, message);
    }

    public <T> T sessionNotFound(CookieDTO cookie) {
        String message = ErrorMessages.SESSION_NOT_FOUND;
        return caster(cookie, message);
    }

    public <T> T sessionExpired(CookieDTO cookie) {
        String message = ErrorMessages.SESSION_EXPIRED;
        return caster(cookie, message);
    }

    public <T> T invalidSession(CookieDTO cookie) {
        String message = ErrorMessages.INVALID_SESSION;
        return caster(cookie, message);
    }

    private <T> T caster(CookieDTO cookie, String errorMessage) {
        Object errorObj = null;

        switch (conversionType) {
            case MESSAGE_DTO:
                errorObj = new MessageDTO(cookie, errorMessage);
                break;
            case CHECK_MESSAGE_DTO:
                errorObj = new CheckMessageDTO(cookie, errorMessage, -1);
                break;
        }

        return (T) errorObj;
    }

    public enum ConversionType {
        MESSAGE_DTO,
        CHECK_MESSAGE_DTO
    }
}
