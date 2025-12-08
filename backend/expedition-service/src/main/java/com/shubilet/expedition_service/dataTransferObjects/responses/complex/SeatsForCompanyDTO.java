package com.shubilet.expedition_service.dataTransferObjects.responses.complex;

import java.util.ArrayList;
import java.util.List;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCompanyDTO;

public class SeatsForCompanyDTO {
    private String message;
    private List<SeatForCompanyDTO> tickets;

    public SeatsForCompanyDTO() {

    }

    public SeatsForCompanyDTO(String message) {
        this.message = message;
        tickets = new ArrayList<>();
    }

    public SeatsForCompanyDTO(String message, List<SeatForCompanyDTO> tickets) {
        this.message = message;
        this.tickets = tickets;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public List<SeatForCompanyDTO> getTickets() {
        return tickets;
    }
    public void setTickets(List<SeatForCompanyDTO> tickets) {
        this.tickets = tickets;
    }
}
