package com.shubilet.api_gateway.controllers;

import com.shubilet.api_gateway.common.constants.ServiceURLs;
import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.AdminRegistrationDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.CompanyRegistrationDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.CustomerRegistrationDTO;


import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.mappers.HttpSessionMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final RestTemplate restTemplate;
    private final HttpSessionMapper httpSessionMapper;

    public RegistrationController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.httpSessionMapper = new  HttpSessionMapper();
    }

    @PostMapping("/customer")
    public ResponseEntity<MessageDTO> registerCustomer(HttpSession httpSession, @RequestBody CustomerRegistrationDTO customerRegistrationDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Starting Customer Registration (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MessageDTO> securityServiceCheckSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionRequest,
                MessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckSessionResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is already an existing logged in session."));
        }
        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckSessionResponse.getStatusCode().is4xxClientError()) {
            logger.info("No user is currently logged in verified (requestId={})", requestId);
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckSessionResponse.getStatusCode()).body(securityServiceCheckSessionResponse.getBody());
        }

        // Send Request to Member Service for Registering a New Customer
        HttpEntity<CustomerRegistrationDTO> memberServiceRequest = new HttpEntity<>(customerRegistrationDTO, headers);
        ResponseEntity<MessageDTO> memberServiceResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_CUSTOMER_REGISTRATION_URL,
                HttpMethod.POST,
                memberServiceRequest,
                MessageDTO.class
        );


        // New Customer has been Successfully Registered on Member Service
        if (memberServiceResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Registration succeeded (requestId={})", requestId);
        } else if (memberServiceResponse.getStatusCode().is4xxClientError()) {
            logger.info("Registration failed (requestId={})", requestId);
            return ResponseEntity.status(memberServiceResponse.getStatusCode()).body(memberServiceResponse.getBody());
        }
        // Something Went Wrong on Member Service
        else if (memberServiceResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceResponse.getStatusCode()).body(memberServiceResponse.getBody());
        }

        // Operation Completed Successfully
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Customer Registration Successful."));
    }

    @PostMapping("/company")
    public ResponseEntity<MessageDTO> registerCompany(HttpSession httpSession, @RequestBody CompanyRegistrationDTO companyRegistrationDTO) {
        String requestId = UUID.randomUUID().toString();

        logger.info("Starting Company Registration (requestId={})", requestId);


        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MessageDTO> securityServiceCheckSessionResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_COMPANY_REGISTRATION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionRequest,
                MessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckSessionResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is already an existing logged in session."));
        }
        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckSessionResponse.getStatusCode().is4xxClientError()) {
            logger.info("No user is currently logged in verified (requestId={})", requestId);
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckSessionResponse.getStatusCode()).body(securityServiceCheckSessionResponse.getBody());
        }

        // Send Request to Member Service for Registering a New Customer
        HttpEntity<CompanyRegistrationDTO> memberServiceRequest = new HttpEntity<>(companyRegistrationDTO, headers);
        ResponseEntity<MessageDTO> memberServiceResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_COMPANY_REGISTRATION_URL,
                HttpMethod.POST,
                memberServiceRequest,
                MessageDTO.class
        );


        // New Customer has been Successfully Registered on Member Service
        if (memberServiceResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Registration succeeded (requestId={})", requestId);
        }
        // Something Went Wrong on Member Service
        else if (memberServiceResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceResponse.getStatusCode()).body(memberServiceResponse.getBody());
        }

        // Operation Completed Successfully
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Company Registration Successful."));
    }

    @PostMapping("/admin")
    public ResponseEntity<MessageDTO> registerAdmin(HttpSession httpSession, @RequestBody AdminRegistrationDTO adminRegistrationDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Starting Admin Registration (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MessageDTO> securityServiceCheckSessionResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_COMPANY_REGISTRATION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionRequest,
                MessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckSessionResponse.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is already an existing logged in session."));
        }
        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckSessionResponse.getStatusCode().is4xxClientError()) {
            logger.info("No user is currently logged in verified (requestId={})", requestId);
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckSessionResponse.getStatusCode()).body(securityServiceCheckSessionResponse.getBody());
        }

        // Send Request to Member Service for Registering a New Customer
        HttpEntity<AdminRegistrationDTO> memberServiceRequest = new HttpEntity<>(adminRegistrationDTO, headers);
        ResponseEntity<MessageDTO> memberServiceResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_ADMIN_REGISTRATION_URL,
                HttpMethod.POST,
                memberServiceRequest,
                MessageDTO.class
        );


        // New Customer has been Successfully Registered on Member Service
        if (memberServiceResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Registration succeeded (requestId={})", requestId);
        }
        // Something Went Wrong on Member Service
        else if (memberServiceResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceResponse.getStatusCode()).body(memberServiceResponse.getBody());
        }

        // Operation Completed Successfully
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Admin Registration Successful."));
    }
}
