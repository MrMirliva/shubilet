package com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations;

import java.util.ArrayList;
import java.util.List;

public class SeatsForCompanyExternalDTO {
    private String message;
    private List<SeatForCompanyExternalDTO> tickets;

    public SeatsForCompanyExternalDTO() {

    }

    public SeatsForCompanyExternalDTO(String message) {
        this.message = message;
        tickets = new ArrayList<>();
    }

    public SeatsForCompanyExternalDTO(String message, List<SeatForCompanyExternalDTO> tickets) {
        this.message = message;
        this.tickets = tickets;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SeatForCompanyExternalDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<SeatForCompanyExternalDTO> tickets) {
        this.tickets = tickets;
    }
}
