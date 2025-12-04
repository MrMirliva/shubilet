package com.shubilet.expedition_service.common.util;

import com.shubilet.expedition_service.common.constants.ErrorMessages;
import com.shubilet.expedition_service.dataTransferObjects.responses.MessageDTO;

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

    public static MessageDTO criticalError() {
        return new MessageDTO(ErrorMessages.CRITICAL_ERROR);
    }

    public static MessageDTO notFound(String entityName) {
        return new MessageDTO(entityName + ErrorMessages.NOT_FOUND);
    }

    public static MessageDTO unauthorized() {
        return new MessageDTO(ErrorMessages.SESSION_NOT_FOUND);
    }

    public static MessageDTO sameCityError() {
        return new MessageDTO(ErrorMessages.SAME_CITY_ERROR_MESSAGE);
    }
}
