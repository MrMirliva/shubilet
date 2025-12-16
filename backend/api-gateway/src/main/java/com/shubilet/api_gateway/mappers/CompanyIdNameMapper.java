package com.shubilet.api_gateway.mappers;

import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.ExpeditionSearchResultCompanyDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.ticket.TicketExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.ticket.TicketsExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.ExpeditionForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.CompanyIdNameMapDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.ExpeditionsForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.ticket.TicketInternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.ticket.TicketsInternalDTO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CompanyIdNameMapper {

    public CompanyIdNameMapper() {
        throw new UnsupportedOperationException("Mapper class cannot be instantiated.");
    }

    public static List<ExpeditionSearchResultCompanyDTO> toExpeditionSearchResultsDTO(ExpeditionsForCustomerDTO expeditionsForCustomerDTO, CompanyIdNameMapDTO companyIdNameMapDTO) {
        List<ExpeditionSearchResultCompanyDTO> matchedExpeditions = new LinkedList<>();
        HashMap<Integer, String> companyMap = companyIdNameMapDTO.getCompanies();

        for (ExpeditionForCustomerDTO expedition : expeditionsForCustomerDTO.getExpeditions()) {
            matchedExpeditions.add(new ExpeditionSearchResultCompanyDTO(
                            expedition.getExpeditionId(),
                            expedition.getDepartureCity(),
                            expedition.getArrivalCity(),
                            expedition.getDate(),
                            expedition.getTime(),
                            expedition.getPrice(),
                            expedition.getDuration(),
                            companyMap.get(expedition.getCompanyId())
                    )
            );
        }
        return matchedExpeditions;
    }

    public static List<TicketExternalDTO> toTicketsExternalDTO(TicketsInternalDTO ticketsInternalDTO, CompanyIdNameMapDTO companyIdNameMapDTO) {
        List<TicketExternalDTO> matchedTickets = new LinkedList<>();
        HashMap<Integer, String> companyMap = companyIdNameMapDTO.getCompanies();

        for (TicketInternalDTO ticket : ticketsInternalDTO.getTicketsDTO()) {
            matchedTickets.add(new TicketExternalDTO(
                            ticket.getPNR(),
                            ticket.getSeatNo(),
                            ticket.getExpeditionId(),
                            companyMap.get(ticket.getCompanyId()),
                            ticket.getDepartureCity(),
                            ticket.getArrivalCity(),
                            ticket.getDate(),
                            ticket.getTime(),
                            ticket.getDuration()
                    )
            );
        }
        return matchedTickets;
    }
}
