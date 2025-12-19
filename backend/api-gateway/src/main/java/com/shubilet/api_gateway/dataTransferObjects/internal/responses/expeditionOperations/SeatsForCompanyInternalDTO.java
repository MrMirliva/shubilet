package com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations;

import java.util.ArrayList;
import java.util.List;

public class SeatsForCompanyInternalDTO {
    private String message;
    private List<SeatForCompanyInternalDTO> tickets;

    public SeatsForCompanyInternalDTO() {

    }

    public SeatsForCompanyInternalDTO(String message) {
        this.message = message;
        tickets = new ArrayList<>();
    }

    public SeatsForCompanyInternalDTO(String message, List<SeatForCompanyInternalDTO> tickets) {
        this.message = message;
        this.tickets = tickets;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SeatForCompanyInternalDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<SeatForCompanyInternalDTO> tickets) {
        this.tickets = tickets;
    }
}
