package com.shubilet.api_gateway.controllers;

import com.shubilet.api_gateway.common.constants.ServiceURLs;
import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.CustomerAttributeDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.MemberCheckMessageDTO;
import com.shubilet.api_gateway.mappers.HttpSessionMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/customer/edit")
public class CustomerProfileManagementController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerProfileManagementController.class);
    private final RestTemplate restTemplate;
    private final HttpSessionMapper httpSessionMapper;

    public CustomerProfileManagementController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.httpSessionMapper = new HttpSessionMapper();
    }

    @PostMapping("/name")
    public ResponseEntity<MessageDTO> customerEditName(HttpSession httpSession, @RequestBody CustomerAttributeDTO customerAttributeDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCustomerRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionCustomerRequest,
                MemberCheckMessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        MemberCheckMessageDTO memberCheckMessageDTO = securityServiceCheckCustomerSessionResponse.getBody();
        customerAttributeDTO.setCustomerId(memberCheckMessageDTO.getUserId());

        HttpEntity<CustomerAttributeDTO> memberServiceCustomerProfileEditNameRequest = new HttpEntity<>(customerAttributeDTO, headers);
        ResponseEntity<MessageDTO> memberServiceCustomerProfileEditNameResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_NAME_URL,
                HttpMethod.POST,
                memberServiceCustomerProfileEditNameRequest,
                MessageDTO.class
        );

        // Customer has been Successfully Updated on Member Service
        if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Profile Name Update Succeeded (requestId={})", requestId);
        } else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is4xxClientError()) {
            logger.info("Customer Profile Name Update Failed (requestId={})", requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        // Something Went Wrong on Member Service
        else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceCustomerProfileEditNameResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Customer Name Successfully Updated"));
    }

    @PostMapping("/surname")
    public ResponseEntity<MessageDTO> customerEditSurname(HttpSession httpSession, @RequestBody CustomerAttributeDTO customerAttributeDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCustomerRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionCustomerRequest,
                MemberCheckMessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        MemberCheckMessageDTO memberCheckMessageDTO = securityServiceCheckCustomerSessionResponse.getBody();
        customerAttributeDTO.setCustomerId(memberCheckMessageDTO.getUserId());

        HttpEntity<CustomerAttributeDTO> memberServiceCustomerProfileEditNameRequest = new HttpEntity<>(customerAttributeDTO, headers);
        ResponseEntity<MessageDTO> memberServiceCustomerProfileEditNameResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_SURNAME_URL,
                HttpMethod.POST,
                memberServiceCustomerProfileEditNameRequest,
                MessageDTO.class
        );

        // Customer has been Successfully Updated on Member Service
        if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Profile Surname Update Succeeded (requestId={})", requestId);
        } else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is4xxClientError()) {
            logger.info("Customer Profile Surname Update Failed (requestId={})", requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        // Something Went Wrong on Member Service
        else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceCustomerProfileEditNameResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Customer Surname Successfully Updated"));
    }

    @PostMapping("/gender")
    public ResponseEntity<MessageDTO> customerEditGender(HttpSession httpSession, @RequestBody CustomerAttributeDTO customerAttributeDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCustomerRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionCustomerRequest,
                MemberCheckMessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        MemberCheckMessageDTO memberCheckMessageDTO = securityServiceCheckCustomerSessionResponse.getBody();
        customerAttributeDTO.setCustomerId(memberCheckMessageDTO.getUserId());

        HttpEntity<CustomerAttributeDTO> memberServiceCustomerProfileEditNameRequest = new HttpEntity<>(customerAttributeDTO, headers);
        ResponseEntity<MessageDTO> memberServiceCustomerProfileEditNameResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_GENDER_URL,
                HttpMethod.POST,
                memberServiceCustomerProfileEditNameRequest,
                MessageDTO.class
        );

        // Customer has been Successfully Updated on Member Service
        if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Profile Gender Update Succeeded (requestId={})", requestId);
        } else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is4xxClientError()) {
            logger.info("Customer Profile Gender Update Failed (requestId={})", requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        // Something Went Wrong on Member Service
        else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceCustomerProfileEditNameResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Customer Gender Successfully Updated"));
    }

    @PostMapping("/email")
    public ResponseEntity<MessageDTO> customerEditEmail(HttpSession httpSession, @RequestBody CustomerAttributeDTO customerAttributeDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCustomerRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionCustomerRequest,
                MemberCheckMessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        MemberCheckMessageDTO memberCheckMessageDTO = securityServiceCheckCustomerSessionResponse.getBody();
        customerAttributeDTO.setCustomerId(memberCheckMessageDTO.getUserId());

        HttpEntity<CustomerAttributeDTO> memberServiceCustomerProfileEditNameRequest = new HttpEntity<>(customerAttributeDTO, headers);
        ResponseEntity<MessageDTO> memberServiceCustomerProfileEditNameResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_EMAIL_URL,
                HttpMethod.POST,
                memberServiceCustomerProfileEditNameRequest,
                MessageDTO.class
        );

        // Customer has been Successfully Updated on Member Service
        if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Profile Email Update Succeeded (requestId={})", requestId);
        } else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is4xxClientError()) {
            logger.info("Customer Profile Email Update Failed (requestId={})", requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        // Something Went Wrong on Member Service
        else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceCustomerProfileEditNameResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Customer Email Successfully Updated"));
    }

    @PostMapping("/password")
    public ResponseEntity<MessageDTO> customerEditPassword(HttpSession httpSession, @RequestBody CustomerAttributeDTO customerAttributeDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCustomerRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionCustomerRequest,
                MemberCheckMessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        MemberCheckMessageDTO memberCheckMessageDTO = securityServiceCheckCustomerSessionResponse.getBody();
        customerAttributeDTO.setCustomerId(memberCheckMessageDTO.getUserId());

        HttpEntity<CustomerAttributeDTO> memberServiceCustomerProfileEditNameRequest = new HttpEntity<>(customerAttributeDTO, headers);
        ResponseEntity<MessageDTO> memberServiceCustomerProfileEditNameResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_PASSWORD_URL,
                HttpMethod.POST,
                memberServiceCustomerProfileEditNameRequest,
                MessageDTO.class
        );

        // Customer has been Successfully Updated on Member Service
        if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Profile Password Update Succeeded (requestId={})", requestId);
        } else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is4xxClientError()) {
            logger.info("Customer Profile Password Update Failed (requestId={})", requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        // Something Went Wrong on Member Service
        else if (memberServiceCustomerProfileEditNameResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceCustomerProfileEditNameResponse.getStatusCode(), requestId);
            return ResponseEntity.status(memberServiceCustomerProfileEditNameResponse.getStatusCode()).body(memberServiceCustomerProfileEditNameResponse.getBody());
        }
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Customer Password Successfully Updated"));
    }
}
