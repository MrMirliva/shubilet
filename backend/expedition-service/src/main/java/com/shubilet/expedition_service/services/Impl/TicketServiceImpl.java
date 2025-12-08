package com.shubilet.expedition_service.services.Impl;

import org.springframework.stereotype.Service;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO;
import com.shubilet.expedition_service.repositories.TicketRepository;
import com.shubilet.expedition_service.services.TicketService;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    public TicketServiceImpl(
        TicketRepository ticketRepository
    ) {
        this.ticketRepository = ticketRepository;
    }

    public TicketDTO getTicketDetails(int expeditionId, int seatNo) {
        return ticketRepository.findTicketDetailsByExpeditionIdAndSeatNo(expeditionId, seatNo);
    }
}
