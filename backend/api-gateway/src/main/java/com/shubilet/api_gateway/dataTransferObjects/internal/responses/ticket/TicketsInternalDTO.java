package com.shubilet.api_gateway.dataTransferObjects.internal.responses.ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketsInternalDTO {
    private String message;
    private List<TicketInternalDTO> ticketsDTO;

    public TicketsInternalDTO(String message) {
        this.message = message;
        this.ticketsDTO = new ArrayList<>();
    }

    public TicketsInternalDTO(String message, List<TicketInternalDTO> ticketsDTO) {
        this.message = message;
        this.ticketsDTO = ticketsDTO;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<TicketInternalDTO> getTicketsDTO() {
        return ticketsDTO;
    }
    public void setTicketsDTO(List<TicketInternalDTO> ticketsDTO) {
        this.ticketsDTO = ticketsDTO;
    }
    
}
