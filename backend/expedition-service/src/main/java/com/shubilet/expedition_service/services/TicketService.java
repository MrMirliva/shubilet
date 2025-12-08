package com.shubilet.expedition_service.services;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO;

public interface TicketService {
    public TicketDTO getTicketDetails(int expeditionId, int seatNo);
}
