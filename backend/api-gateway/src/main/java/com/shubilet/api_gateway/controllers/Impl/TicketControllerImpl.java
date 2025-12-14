package com.shubilet.api_gateway.controllers.Impl;

import com.shubilet.api_gateway.common.constants.ServiceURLs;
import com.shubilet.api_gateway.controllers.TicketController;
import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.expeditionOperations.BuyTicketExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.ticket.BuyTicketInternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.ticket.TicketInfoDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.auth.MemberCheckMessageDTO;
import com.shubilet.api_gateway.managers.HttpSessionManager;
import com.shubilet.api_gateway.mappers.BuyTicketExternalMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/ticket")
public class TicketControllerImpl implements TicketController {
    private final Logger logger = LoggerFactory.getLogger(TicketControllerImpl.class);
    private final RestTemplate restTemplate;
    private final HttpSessionManager httpSessionManager;
    private final BuyTicketExternalMapper buyTicketExternalMapper;

    public TicketControllerImpl(RestTemplate restTemplate, BuyTicketExternalMapper buyTicketExternalMapper) {
        this.restTemplate = restTemplate;
        this.buyTicketExternalMapper = buyTicketExternalMapper;
        this.httpSessionManager = new HttpSessionManager();

    }

    @Override
    public ResponseEntity<MessageDTO> sendTicketDetailsForCustomer(HttpSession httpSession) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
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

        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Success"));
    }

    @PostMapping("/buy")
    @Override
    public ResponseEntity<TicketInfoDTO> buyTicketForCustomer(HttpSession httpSession, BuyTicketExternalDTO buyTicketExternalDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Expedition Search (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
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
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new TicketInfoDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new TicketInfoDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        BuyTicketInternalDTO buyTicketInternalDTO = buyTicketExternalMapper.toBuyTicketInternalDTO(
                buyTicketExternalDTO,
                securityServiceCheckCustomerSessionResponse.getBody()
        );

        HttpEntity<BuyTicketInternalDTO> expeditionServiceTicketBuyRequest = new HttpEntity<>(buyTicketInternalDTO, headers);
        ResponseEntity<TicketInfoDTO> expeditionServiceTicketBuyResponse = restTemplate.exchange(
                ServiceURLs.EXPEDITION_SERVICE_BUY_TICKET,
                HttpMethod.POST,
                expeditionServiceTicketBuyRequest,
                TicketInfoDTO.class
        );

        // Session Existence Clarified by Expedition Service
        if (expeditionServiceTicketBuyResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Ticket Sale Successful (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Expedition Service
        if (expeditionServiceTicketBuyResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(expeditionServiceTicketBuyResponse.getBody());
        }

        // Something Went Wrong on Expedition Service
        if (expeditionServiceTicketBuyResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(expeditionServiceTicketBuyResponse.getBody());
        }

        return ResponseEntity.status(HttpStatus.OK).body(expeditionServiceTicketBuyResponse.getBody());
    }
}
