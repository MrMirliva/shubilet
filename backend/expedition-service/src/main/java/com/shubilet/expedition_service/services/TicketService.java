package com.shubilet.expedition_service.services;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO;

public interface TicketService {
    public TicketDTO getTicketDetails(String ticketPNR);

    public String generateTicket(int expeditionId, int seatNo, int customerId);
}
