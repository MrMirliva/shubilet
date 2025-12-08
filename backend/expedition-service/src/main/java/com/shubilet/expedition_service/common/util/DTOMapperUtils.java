package com.shubilet.expedition_service.common.util;

import java.util.List;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.ExpeditionForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.ExpeditionForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO;

import com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.*;

public class DTOMapperUtils {
    
    private DTOMapperUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static ExpeditionForCompanyDTO toExpeditionForCompanyDTO(ExpeditionForCompanyRepoDTO repoDTO) {
        int expeditionId = 0;
        String departureCity = "";
        String arrivalCity = "";
        String date = "";
        String time = "";
        double price = 0.0;
        int duration = 0;
        int capacity = 0;
        int numberOfBookedSeats = 0;
        double profit = 0.0;

        if (repoDTO != null) {
            expeditionId = (repoDTO.getExpeditionId() != null) ? repoDTO.getExpeditionId() : 0;
            departureCity = (repoDTO.getDepartureCity() != null) ? repoDTO.getDepartureCity() : "";
            arrivalCity = (repoDTO.getArrivalCity() != null) ? repoDTO.getArrivalCity() : "";

            if (repoDTO.getDateAndTime() != null) {
                String[] dateTimeParts = repoDTO.getDateAndTime().toString().split("T");
                date = dateTimeParts[0];
                time = dateTimeParts[1];
            }

            price = (repoDTO.getPrice() != null) ? repoDTO.getPrice().doubleValue() : 0.0;
            duration = (repoDTO.getDuration() != null) ? repoDTO.getDuration() : 0;
            capacity = (repoDTO.getCapacity() != null) ? repoDTO.getCapacity() : 0;
            numberOfBookedSeats = (repoDTO.getNumberOfBookedSeats() != null) ? repoDTO.getNumberOfBookedSeats() : 0;
            profit = (repoDTO.getProfit() != null) ? repoDTO.getProfit().doubleValue() : 0.0;
        }

        return new ExpeditionForCompanyDTO(
            expeditionId,
            departureCity,
            arrivalCity,
            date,
            time,
            price,
            duration,
            capacity,
            numberOfBookedSeats,
            profit
        );
    }

    public static List<ExpeditionForCompanyDTO> toExpeditionForCompanyDTO(List<ExpeditionForCompanyRepoDTO> repoDTOs) {
        return repoDTOs.stream()
                .map(DTOMapperUtils::toExpeditionForCompanyDTO)
                .toList();
    }

    public static ExpeditionForCustomerDTO toExpeditionForCustomerDTO(ExpeditionForCustomerRepoDTO repoDTO) {
        int expeditionId = 0;
        String departureCity = "";
        String arrivalCity = "";
        String date = "";
        String time = "";
        double price = 0.0;
        int duration = 0;
        int companyId = 0;

        if (repoDTO != null) {
            expeditionId = (repoDTO.getExpeditionId() != null) ? repoDTO.getExpeditionId() : 0;
            departureCity = (repoDTO.getDepartureCity() != null) ? repoDTO.getDepartureCity() : "";
            arrivalCity = (repoDTO.getArrivalCity() != null) ? repoDTO.getArrivalCity() : "";

            if (repoDTO.getDateAndTime() != null) {
                String[] dateTimeParts = repoDTO.getDateAndTime().toString().split("T");
                date = dateTimeParts[0];
                time = dateTimeParts[1];
            }

            price = (repoDTO.getPrice() != null) ? repoDTO.getPrice().doubleValue() : 0.0;
            duration = (repoDTO.getDuration() != null) ? repoDTO.getDuration() : 0;
            companyId = (repoDTO.getCompanyId() != null) ? repoDTO.getCompanyId() : 0;
        }

        return new ExpeditionForCustomerDTO(
            expeditionId,
            departureCity,
            arrivalCity,
            date,
            time,
            price,
            duration,
            companyId
        );
    }

    public static List<ExpeditionForCustomerDTO> toExpeditionForCustomerDTO(List<ExpeditionForCustomerRepoDTO> repoDTOs) {
        return repoDTOs.stream()
                .map(DTOMapperUtils::toExpeditionForCustomerDTO)
                .toList();
    }

    public static SeatForCompanyDTO toSeatForCompanyDTO(SeatForCompanyRepoDTO repoDTO) {
        int seatId = 0;
        int expeditionId = 0;
        int seatNo = 0;
        int customerId = 0;
        String status = "";
        
        if (repoDTO != null) {
            seatId = (repoDTO.getSeatId() != null) ? repoDTO.getSeatId() : 0;
            expeditionId = (repoDTO.getExpeditionId() != null) ? repoDTO.getExpeditionId() : 0;
            seatNo = (repoDTO.getSeatNo() != null) ? repoDTO.getSeatNo() : 0;
            customerId = (repoDTO.getCustomerId() != null) ? repoDTO.getCustomerId() : 0;
            status = (repoDTO.getStatus() != null) ? repoDTO.getStatus().toString() : "";
        }

        return new SeatForCompanyDTO(
            seatId,
            expeditionId,
            seatNo,
            customerId,
            status
        );
    }

    public static List<SeatForCompanyDTO> toSeatForCompanyDTO(List<SeatForCompanyRepoDTO> repoDTOs) {
        return repoDTOs.stream()
                .map(DTOMapperUtils::toSeatForCompanyDTO)
                .toList();
    }

    public static TicketDTO toTicketDTO(TicketRepoDTO repoDTO) {
        String PNR = "";
        int seatNo = 0;
        int expeditionId = 0;
        int companyId = 0;
        String departureCity = "";
        String arrivalCity = "";
        String date = "";
        String time = "";
        int duration = 0;

        if (repoDTO != null) {
            PNR = (repoDTO.getPNR() != null) ? repoDTO.getPNR() : "";
            seatNo = (repoDTO.getSeatNo() != null) ? repoDTO.getSeatNo() : 0;
            expeditionId = (repoDTO.getExpeditionId() != null) ? repoDTO.getExpeditionId() : 0;
            companyId = (repoDTO.getCompanyId() != null) ? repoDTO.getCompanyId() : 0;
            departureCity = (repoDTO.getDepartureCity() != null) ? repoDTO.getDepartureCity() : "";
            arrivalCity = (repoDTO.getArrivalCity() != null) ? repoDTO.getArrivalCity() : "";

            if (repoDTO.getDateAndTime() != null) {
                String[] dateTimeParts = repoDTO.getDateAndTime().toString().split("T");
                date = dateTimeParts[0];
                time = dateTimeParts[1];
            }

            duration = (repoDTO.getDuration() != null) ? repoDTO.getDuration() : 0;
        }

        return new TicketDTO(
            PNR,
            seatNo,
            expeditionId,
            companyId,
            departureCity,
            arrivalCity,
            date,
            time,
            duration
        );
    }

    public static List<TicketDTO> toTicketDTO(List<TicketRepoDTO> repoDTOs) {
        return repoDTOs.stream()
                .map(DTOMapperUtils::toTicketDTO)
                .toList();
    }

    public static SeatForCustomerDTO toSeatForCustomerDTO(SeatForCustomerRepoDTO repoDTO) {
        int customerId = 0;
        int expeditionId = 0;
        int seatNo = 0;
        String status = "";

        if (repoDTO != null) {
            customerId = (repoDTO.getCustomerId() != null) ? repoDTO.getCustomerId() : 0;
            expeditionId = (repoDTO.getExpeditionId() != null) ? repoDTO.getExpeditionId() : 0;
            seatNo = (repoDTO.getSeatNo() != null) ? repoDTO.getSeatNo() : 0;
            status = (repoDTO.getStatus() != null) ? repoDTO.getStatus().toString() : "";
        }

        return new SeatForCustomerDTO(
            customerId,
            expeditionId,
            seatNo,
            status
        );
    }

    public static List<SeatForCustomerDTO> toSeatForCustomerDTO(List<SeatForCustomerRepoDTO> repoDTOs) {
        return repoDTOs.stream()
                .map(DTOMapperUtils::toSeatForCustomerDTO)
                .toList();
    }
    
}
