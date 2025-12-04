package com.shubilet.expedition_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.expedition_service.dataTransferObjects.requests.ViewDetailsForCustomerDTO;

public interface ViewForCustomerController {

    public ResponseEntity<?> viewAvailableExpeditions(ViewDetailsForCustomerDTO viewDetailsForCustomerDTO);

    public ResponseEntity<?> viewAvailableSeats(int expeditionId);

}
