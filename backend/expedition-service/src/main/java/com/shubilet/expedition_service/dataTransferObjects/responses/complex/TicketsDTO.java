package com.shubilet.expedition_service.dataTransferObjects.responses.complex;

import java.util.ArrayList;
import java.util.List;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO;

public class TicketsDTO {
    private String message;
    private List<TicketDTO> ticketsDTO;

    public TicketsDTO(String message) {
        this.message = message;
        this.ticketsDTO = new ArrayList<>();
    }

    public TicketsDTO(String message, List<TicketDTO> ticketsDTO) {
        this.message = message;
        this.ticketsDTO = ticketsDTO;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<TicketDTO> getTicketsDTO() {
        return ticketsDTO;
    }
    public void setTicketsDTO(List<TicketDTO> ticketsDTO) {
        this.ticketsDTO = ticketsDTO;
    }
    
}
