package com.shubilet.expedition_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.expedition_service.dataTransferObjects.requests.CustomerIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ViewDetailsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.ExpeditionsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.SeatsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.TicketsDTO;

public interface ViewForCustomerController {

    public ResponseEntity<ExpeditionsForCustomerDTO> viewAvailableExpeditions(ViewDetailsForCustomerDTO viewDetailsForCustomerDTO);

    public ResponseEntity<SeatsForCustomerDTO> viewAvailableSeats(ExpeditionIdDTO expeditionIdDTO);

    public ResponseEntity<TicketsDTO> viewAllTickets(CustomerIdDTO customerIdDTO);
}
