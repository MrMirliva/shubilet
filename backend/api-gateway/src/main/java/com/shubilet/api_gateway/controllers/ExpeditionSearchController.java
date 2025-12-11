package com.shubilet.api_gateway.controllers;

import com.shubilet.api_gateway.common.constants.ServiceURLs;
import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.ExpeditionSearchDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.ExpeditionsForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.MemberCheckMessageDTO;
import com.shubilet.api_gateway.mappers.HttpSessionMapper;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
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
@RequestMapping("/api/expedition/search")
public class ExpeditionSearchController {
    private final Logger logger = LoggerFactory.getLogger(ExpeditionSearchController.class);
    private final RestTemplate restTemplate;
    private final HttpSessionMapper httpSessionMapper;

    public ExpeditionSearchController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.httpSessionMapper = new HttpSessionMapper();
    }

    @PostMapping("")
    public ResponseEntity<MessageDTO> sendExpeditions(HttpSession httpSession, @RequestBody ExpeditionSearchDTO expeditionSearchDTO) {
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

        // Session Existence Clarified by Security Service
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

        HttpEntity<ExpeditionSearchDTO> expeditionServiceSearchExpeditionRequest = new HttpEntity<>(expeditionSearchDTO, headers);
        ResponseEntity<ExpeditionsForCustomerDTO> expeditionServiceSearchExpeditionResponse = restTemplate.exchange(
                ServiceURLs.EXPEDITION_SERVICE_SEARCH_URL,
                HttpMethod.POST,
                expeditionServiceSearchExpeditionRequest,
                ExpeditionsForCustomerDTO.class
        );

        // Successfully Returned Expeditions from Expedition Service
        if (expeditionServiceSearchExpeditionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Expedition Successfully Retrieved (requestId={})", requestId);
        }
        else if (expeditionServiceSearchExpeditionResponse.getStatusCode().is4xxClientError()) {
            logger.info("Bad Request for Expedition Service (requestId={})", requestId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO(expeditionServiceSearchExpeditionResponse.getBody().getMessage()));
        }
        else  if (expeditionServiceSearchExpeditionResponse.getStatusCode().is5xxServerError()) {
            logger.info("Internal Server Error of Expedition Service (requestId={})", requestId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDTO(expeditionServiceSearchExpeditionResponse.getBody().getMessage()));
        }


        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Success"));
    }
}
