package com.shubilet.security_service.common.util;

import com.shubilet.security_service.common.constants.ErrorMessages;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

public final class ErrorUtils {
    private ErrorUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static MessageDTO isNull(String fieldName) {
        return new MessageDTO(fieldName + ErrorMessages.NULL_OR_EMPTY);
    }
    
    public static MessageDTO isInvalidFormat(String fieldName) {
        return new MessageDTO(fieldName + ErrorMessages.INVALID_FORMAT);
    }

    public static MessageDTO userAlreadyLoggedIn() {
        return new MessageDTO(ErrorMessages.USER_ALREADY_LOGGED_IN);
    }

    public static MessageDTO notFound(String entityName) {
        return new MessageDTO(entityName + ErrorMessages.NOT_FOUND);
    }

    public static MessageDTO isIncorrect(String fieldName) {
        return new MessageDTO(fieldName + ErrorMessages.INCORRECT);
    }
}
