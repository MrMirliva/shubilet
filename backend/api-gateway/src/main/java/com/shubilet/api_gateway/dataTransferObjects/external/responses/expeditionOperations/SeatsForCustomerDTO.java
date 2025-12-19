package com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations;

import java.util.ArrayList;
import java.util.List;

public class SeatsForCustomerDTO {
    private String message;
    private List<SeatForCustomerDTO> tickets;

    public SeatsForCustomerDTO() {

    }

    public SeatsForCustomerDTO(String message) {
        this.message = message;
        tickets = new ArrayList<>();
    }

    public SeatsForCustomerDTO(String message, List<SeatForCustomerDTO> tickets) {
        this.message = message;
        this.tickets = tickets;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<SeatForCustomerDTO> getTickets() {
        return tickets;
    }
    public void setTickets(List<SeatForCustomerDTO> tickets) {
        this.tickets = tickets;
    }
}
