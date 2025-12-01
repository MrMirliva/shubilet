package com.shubilet.member_service.controllers;

import com.shubilet.member_service.dataTransferObjects.requests.AdminRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CompanyRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CustomerRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.responses.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface RegistrationController {

    /**
     * Creates a user on Customer Table with Given Information
     *
     * @param customerRegistrationDTO Holds the Necessary Information for New Customer Registration
     * @return HTTP Response
     *
     */
    public ResponseEntity<MessageDTO> registerCustomer(@RequestBody CustomerRegistrationDTO customerRegistrationDTO);/**

     * Creates a user on Company Table with Given Information
     *
     * @param companyRegistrationDTO Holds the Necessary Information for New Customer Registration
     * @return HTTP Response
     *
     */
    public ResponseEntity<MessageDTO> registerCompany(@RequestBody CompanyRegistrationDTO companyRegistrationDTO);

    /**
     * Creates a user on Admin Table with Given Information
     *
     * @param adminRegistrationDTO Holds the Necessary Information for New Customer Registration
     * @return HTTP Response
     *
     */
    public ResponseEntity<MessageDTO> registerAdmin(@RequestBody AdminRegistrationDTO adminRegistrationDTO);
}
