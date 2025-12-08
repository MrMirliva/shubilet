package com.shubilet.expedition_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.expedition_service.dataTransferObjects.requests.ExpeditionIdDTO;
import com.shubilet.expedition_service.dataTransferObjects.requests.ViewDetailsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.ExpeditionsForCustomerDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.complex.SeatsForCustomerDTO;

public interface ViewForCustomerController {

    public ResponseEntity<ExpeditionsForCustomerDTO> viewAvailableExpeditions(ViewDetailsForCustomerDTO viewDetailsForCustomerDTO);

    public ResponseEntity<SeatsForCustomerDTO> viewAvailableSeats(ExpeditionIdDTO expeditionIdDTO);

}
