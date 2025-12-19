package com.shubilet.api_gateway.mappers;

import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.ExpeditionSearchResultCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.SeatForCompanyExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.SeatForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.SeatsForCompanyExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.ticket.TicketExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.*;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.ticket.TicketInternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.ticket.TicketsInternalDTO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CustomerIdNameMapper {

    public CustomerIdNameMapper() {
        throw new UnsupportedOperationException("Mapper class cannot be instantiated.");
    }

    public static List<SeatForCompanyExternalDTO> toSeatsForCompanyExternalDTO(SeatsForCompanyInternalDTO seatsForCompanyInternalDTO, CustomerIdNameMapDTO customerIdNameMapDTO) {
        List<SeatForCompanyExternalDTO> matchedSeats = new LinkedList<>();
        HashMap<Integer, String> customerMap = customerIdNameMapDTO.getCustomers();

        for (SeatForCompanyInternalDTO seat : seatsForCompanyInternalDTO.getTickets()) {
            matchedSeats.add(new SeatForCompanyExternalDTO(
                    seat.getSeatId(),
                    seat.getExpeditionId(),
                    seat.getSeatNo(),
                    customerMap.get(seat.getCustomerId()),
                    seat.getStatus()));
        }
        return matchedSeats;
    }
}
