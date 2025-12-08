package com.shubilet.expedition_service.common.util;

import com.shubilet.expedition_service.common.constants.ErrorMessages;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.CardsDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.ExpeditionsForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.ExpeditionsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.SeatsForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.SeatsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.message.MessageDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.middle.ExpeditionInfoForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.middle.TicketInfoDTO;

public class ErrorUtils {
    private ConversionType conversionType;

    public ErrorUtils(ConversionType conversionType) {
        this.conversionType = conversionType;
    }

    public <T> T isNull(String fieldName) {
        String message = fieldName + ErrorMessages.NULL_OR_EMPTY;
        return caster(message);
    }

    public <T> T isInvalidFormat(String fieldName) {
        String message = fieldName + ErrorMessages.INVALID_FORMAT;
        return caster(message);
    }

    public <T> T criticalError() {
        String message = ErrorMessages.CRITICAL_ERROR;
        return caster(message);
    }

    public <T> T notFound(String fieldName) {
        String message = fieldName + ErrorMessages.NOT_FOUND;
        return caster(message);
    }

    public <T> T unauthorized() {
        String message = ErrorMessages.SESSION_NOT_FOUND;
        return caster(message);
    }

    public <T> T sameCityError() {
        String message = ErrorMessages.SAME_CITY_ERROR_MESSAGE;
        return caster(message);
    }

    public <T> T alreadyExists(String entityName) {
        String message = entityName + ErrorMessages.ALREADY_EXISTS;
        return caster(message);
    }

    public <T> T alreadyBooked(String entityName) {
        String message = entityName + ErrorMessages.ALREADY_BOOKED;
        return caster(message);
    }



    private <T> T caster(String errorMessage) {
        Object errorObj = null;

        switch (conversionType) {
            case MESSAGE_DTO:
                errorObj = new MessageDTO(errorMessage);
                break;
            case EXPEDITION_INFO_FOR_COMPANY_DTO:
                errorObj = new ExpeditionInfoForCompanyDTO(errorMessage);
                break;
            case TICKET_INFO_DTO:
                errorObj = new TicketInfoDTO(errorMessage);
                break;
            case CARDS_DTO:
                errorObj = new CardsDTO(errorMessage);
                break;
            case EXPEDITIONS_FOR_COMPANY_DTO:
                errorObj = new ExpeditionsForCompanyDTO(errorMessage);
                break;
            case EXPEDITIONS_FOR_CUSTOMER_DTO:
                errorObj = new ExpeditionsForCustomerDTO(errorMessage);
                break;
            case SEATS_FOR_CUSTOMER_DTO:
                errorObj = new SeatsForCustomerDTO(errorMessage);
                break;
            case SEATS_FOR_COMPANY_DTO:
                errorObj = new SeatsForCompanyDTO(errorMessage);
                break;
            default:
                throw new IllegalArgumentException("Unsupported conversion type");
        }

        return (T) errorObj;
    }

    public enum ConversionType {
        MESSAGE_DTO,
        EXPEDITION_INFO_FOR_COMPANY_DTO,
        TICKET_INFO_DTO,
        CARDS_DTO,
        EXPEDITIONS_FOR_COMPANY_DTO,
        EXPEDITIONS_FOR_CUSTOMER_DTO,
        SEATS_FOR_CUSTOMER_DTO,
        SEATS_FOR_COMPANY_DTO
    }
}
