package com.shubilet.member_service.controllers;

import com.shubilet.member_service.dataTransferObjects.requests.CustomerCreationDTO;
import com.shubilet.member_service.dataTransferObjects.responses.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface CustomerController {

    /**
     * Creates a user on Customer Table with Given Information
     *
     * @param customerCreationDTO Holds the Necessary Information for New Customer Registration
     * @return HTTP Response
     *
     */
    public ResponseEntity<MessageDTO> createCustomer(@RequestBody CustomerCreationDTO customerCreationDTO);
}
