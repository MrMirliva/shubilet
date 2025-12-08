package com.shubilet.expedition_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.expedition_service.dataTransferObjects.requests.BuyTicketDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.CustomerIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.CardsDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.middle.TicketInfoDTO;

public interface RezervationController {

    public ResponseEntity<TicketInfoDTO> buyTicket(BuyTicketDTO buyTicketDTO);

    public ResponseEntity<CardsDTO> viewCards(CustomerIdDTO customerIdDTO);
}
