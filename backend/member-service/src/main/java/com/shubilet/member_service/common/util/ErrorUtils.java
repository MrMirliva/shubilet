package com.shubilet.member_service.common.util;

import com.shubilet.member_service.common.constants.ErrorMessages;
import com.shubilet.member_service.dataTransferObjects.responses.MessageDTO;

public final class ErrorUtils {
    private ErrorUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static MessageDTO isNull(String fieldName) {
        return new MessageDTO(fieldName + ErrorMessages.NULL_OR_EMPTY);
    }

    public static MessageDTO isInvalidFormat(String fieldName) {
        return new MessageDTO(fieldName + ErrorMessages.INVALID_FORMAT);
    }

    public static MessageDTO alreadyExists(String objectName) {
        return new MessageDTO(objectName + ErrorMessages.ALREADY_EXISTS);
    }

    public static MessageDTO criticalError() {
        return new MessageDTO(ErrorMessages.CRITICAL_ERROR);
    }

}
