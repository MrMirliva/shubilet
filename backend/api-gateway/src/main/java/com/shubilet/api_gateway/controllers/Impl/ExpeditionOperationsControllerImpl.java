package com.shubilet.api_gateway.controllers.Impl;

import com.shubilet.api_gateway.common.constants.ServiceURLs;
import com.shubilet.api_gateway.controllers.ExpeditionOperationsController;
import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.expeditionOperations.ExpeditionCreationExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.expeditionOperations.ExpeditionIdDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.expeditionOperations.ExpeditionSearchDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.ExpeditionSearchResultCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.ExpeditionSearchResultsCompanyDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.ExpeditionsForCompanyDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.SeatsForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.CompanyIdDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.expeditionOperations.ExpeditionCreationInternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.CompanyIdNameMapDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.ExpeditionsForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.auth.MemberCheckMessageDTO;
import com.shubilet.api_gateway.managers.HttpSessionManager;
import com.shubilet.api_gateway.mappers.CompanyIdNameMapper;
import com.shubilet.api_gateway.mappers.expeditionOperations.ExpeditionCreationExternalMapper;
import com.shubilet.api_gateway.mappers.expeditionOperations.ExpeditionSearchCompanyResponseMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/expedition")
public class ExpeditionOperationsControllerImpl implements ExpeditionOperationsController {
    public final Logger logger = LoggerFactory.getLogger(ExpeditionOperationsControllerImpl.class);
    public final RestTemplate restTemplate;
    public final HttpSessionManager httpSessionManager;
    public final ExpeditionCreationExternalMapper expeditionCreationExternalMapper;
    private final ExpeditionSearchCompanyResponseMapper expeditionSearchCompanyResponseMapper;


    public ExpeditionOperationsControllerImpl(RestTemplate restTemplate, ExpeditionCreationExternalMapper expeditionCreationExternalMapper, ExpeditionSearchCompanyResponseMapper expeditionSearchCompanyResponseMapper) {
        this.restTemplate = restTemplate;
        this.expeditionCreationExternalMapper = expeditionCreationExternalMapper;
        this.expeditionSearchCompanyResponseMapper = expeditionSearchCompanyResponseMapper;
        this.httpSessionManager = new HttpSessionManager();
    }

    @PostMapping("/create")
    @Override
    public ResponseEntity<MessageDTO> createExpedition(HttpSession httpSession, @RequestBody ExpeditionCreationExternalDTO expeditionCreationExternalDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Expedition Search (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCompanyRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCompanySessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_COMPANY_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionCompanyRequest,
                MemberCheckMessageDTO.class
        );

        cookieDTO = securityServiceCheckCompanySessionResponse.getBody().getCookie();
        httpSessionManager.updateSessionCookie(httpSession, cookieDTO);

        // Session Existence Clarified by Security Service
        if (securityServiceCheckCompanySessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCompanySessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new MessageDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCompanySessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity.status(securityServiceCheckCompanySessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckCompanySessionResponse.getBody().getMessage()));
        }
        ExpeditionCreationInternalDTO expeditionCreationInternalDTO = expeditionCreationExternalMapper.toExpeditionCreationInternalDTO(
                expeditionCreationExternalDTO,
                securityServiceCheckCompanySessionResponse.getBody()
        );
        HttpEntity<ExpeditionCreationInternalDTO> expeditionServiceExpeditionCreationInternalRequest = new HttpEntity<>(expeditionCreationInternalDTO, headers);
        ResponseEntity<MessageDTO> expeditionServiceExpeditionCreationInternalResponse = restTemplate.exchange(
                ServiceURLs.EXPEDITION_SERVICE_CREATE_EXPEDITION_URL,
                HttpMethod.POST,
                expeditionServiceExpeditionCreationInternalRequest,
                MessageDTO.class
        );

        if (expeditionServiceExpeditionCreationInternalResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Expedition Creation Successful (requestId={})", requestId);

        } else if (expeditionServiceExpeditionCreationInternalResponse.getStatusCode().is4xxClientError()) {
            logger.info("Expedition Creation Failed (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceExpeditionCreationInternalResponse.getStatusCode())
                    .body(new MessageDTO(expeditionServiceExpeditionCreationInternalResponse.getBody().getMessage()));

        } else if (expeditionServiceExpeditionCreationInternalResponse.getStatusCode().is5xxServerError()) {
            logger.info("Expedition Creation Failed at Expedition Service (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceExpeditionCreationInternalResponse.getStatusCode())
                    .body(new MessageDTO(expeditionServiceExpeditionCreationInternalResponse.getBody().getMessage()));

        }
        return ResponseEntity.status(HttpStatus.OK).body(expeditionServiceExpeditionCreationInternalResponse.getBody());
    }

    @PostMapping("/customer/get/search/expeditions")
    @Override
    public ResponseEntity<ExpeditionSearchResultsCompanyDTO> sendExpeditions(HttpSession httpSession, @RequestBody ExpeditionSearchDTO expeditionSearchDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Expedition Search (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckCustomerSessionRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckCustomerSessionRequest,
                MemberCheckMessageDTO.class
        );

        cookieDTO = securityServiceCheckCustomerSessionResponse.getBody().getCookie();
        httpSessionManager.updateSessionCookie(httpSession, cookieDTO);

        // Session Existence Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity
                    .status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new ExpeditionSearchResultsCompanyDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity
                    .status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new ExpeditionSearchResultsCompanyDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        HttpEntity<ExpeditionSearchDTO> expeditionServiceSearchExpeditionRequest = new HttpEntity<>(expeditionSearchDTO, headers);
        ResponseEntity<ExpeditionsForCustomerDTO> expeditionServiceSearchExpeditionResponse = restTemplate.exchange(
                ServiceURLs.EXPEDITION_SERVICE_SEARCH_EXPEDITION_URL,
                HttpMethod.POST,
                expeditionServiceSearchExpeditionRequest,
                ExpeditionsForCustomerDTO.class
        );

        // Successfully Returned Expeditions from Expedition Service
        if (expeditionServiceSearchExpeditionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Expedition Successfully Retrieved (requestId={})", requestId);

        } else if (expeditionServiceSearchExpeditionResponse.getStatusCode().is4xxClientError()) {
            logger.info("Bad Request for Expedition Service (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceSearchExpeditionResponse.getStatusCode())
                    .body(new ExpeditionSearchResultsCompanyDTO(expeditionServiceSearchExpeditionResponse.getBody().getMessage()));

        } else if (expeditionServiceSearchExpeditionResponse.getStatusCode().is5xxServerError()) {
            logger.info("Internal Server Error of Expedition Service (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceSearchExpeditionResponse.getStatusCode())
                    .body(new ExpeditionSearchResultsCompanyDTO(expeditionServiceSearchExpeditionResponse.getBody().getMessage()));
        }

        List<CompanyIdDTO> companyIdDTOs = expeditionSearchCompanyResponseMapper.toCompanyIdDTOs(expeditionServiceSearchExpeditionResponse.getBody().getExpeditions());

        HttpEntity<List<CompanyIdDTO>> memberServiceGetCompanyNamesRequest = new HttpEntity<>(companyIdDTOs, headers);
        ResponseEntity<CompanyIdNameMapDTO> memberServiceGetCompanyNamesResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_GET_COMPANY_NAMES_URL,
                HttpMethod.POST,
                memberServiceGetCompanyNamesRequest,
                CompanyIdNameMapDTO.class
        );

        if (memberServiceGetCompanyNamesResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Company Names Successfully Retrieved (requestId={})", requestId);

        } else if (memberServiceGetCompanyNamesResponse.getStatusCode().is4xxClientError()) {
            logger.warn("Bad Request for Member Service (requestId={})", requestId);
            return ResponseEntity
                    .status(memberServiceGetCompanyNamesResponse.getStatusCode())
                    .body(new ExpeditionSearchResultsCompanyDTO(memberServiceGetCompanyNamesResponse.getBody().getMessage()));

        } else if (memberServiceGetCompanyNamesResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Internal Server Error of Member Service (requestId={})", requestId);
            return ResponseEntity
                    .status(memberServiceGetCompanyNamesResponse.getStatusCode())
                    .body(new ExpeditionSearchResultsCompanyDTO(memberServiceGetCompanyNamesResponse.getBody().getMessage()));
        }


        List<ExpeditionSearchResultCustomerDTO> expeditionSearchResults = CompanyIdNameMapper.toExpeditionSearchResultsDTO(
                expeditionServiceSearchExpeditionResponse.getBody(),
                memberServiceGetCompanyNamesResponse.getBody()
        );
        return ResponseEntity.status(HttpStatus.OK).body(new ExpeditionSearchResultsCompanyDTO("Success", expeditionSearchResults));
    }


    @PostMapping("/customer/get/search/seats")
    @Override
    public ResponseEntity<SeatsForCustomerDTO> sendSeats(HttpSession httpSession, @RequestBody ExpeditionIdDTO expeditionIdDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Expedition Search (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckCustomerSessionRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckCustomerSessionRequest,
                MemberCheckMessageDTO.class
        );

        cookieDTO = securityServiceCheckCustomerSessionResponse.getBody().getCookie();
        httpSessionManager.updateSessionCookie(httpSession, cookieDTO);

        // Session Existence Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity
                    .status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new SeatsForCustomerDTO("There is no Existing Customer Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity
                    .status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new SeatsForCustomerDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }

        HttpEntity<ExpeditionIdDTO> expeditionServiceSearchSeatRequest = new HttpEntity<>(expeditionIdDTO, headers);
        ResponseEntity<SeatsForCustomerDTO> expeditionServiceSeatExpeditionResponse = restTemplate.exchange(
                ServiceURLs.EXPEDITION_SERVICE_SEARCH_SEAT_URL,
                HttpMethod.POST,
                expeditionServiceSearchSeatRequest,
                SeatsForCustomerDTO.class
        );

        // Successfully Returned Expeditions from Expedition Service
        if (expeditionServiceSeatExpeditionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Expedition Seats Successfully Retrieved (requestId={})", requestId);

        } else if (expeditionServiceSeatExpeditionResponse.getStatusCode().is4xxClientError()) {
            logger.info("Bad Request for Expedition Service (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceSeatExpeditionResponse.getStatusCode())
                    .body(new SeatsForCustomerDTO(expeditionServiceSeatExpeditionResponse.getBody().getMessage()));

        } else if (expeditionServiceSeatExpeditionResponse.getStatusCode().is5xxServerError()) {
            logger.info("Internal Server Error of Expedition Service (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceSeatExpeditionResponse.getStatusCode())
                    .body(new SeatsForCustomerDTO(expeditionServiceSeatExpeditionResponse.getBody().getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(expeditionServiceSeatExpeditionResponse.getBody());
    }



    @PostMapping("/company/get/all")
    @Override
    public ResponseEntity<ExpeditionsForCompanyDTO> sendCompanyExpeditions(HttpSession httpSession) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Getting Company Expeditions (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCompanyRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCompanySessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_COMPANY_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionCompanyRequest,
                MemberCheckMessageDTO.class
        );

        // Session Existence Clarified by Security Service
        if (securityServiceCheckCompanySessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Company Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckCompanySessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ExpeditionsForCompanyDTO(securityServiceCheckCompanySessionResponse.getBody().getMessage()));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCompanySessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity
                    .status(securityServiceCheckCompanySessionResponse.getStatusCode())
                    .body(new ExpeditionsForCompanyDTO(securityServiceCheckCompanySessionResponse.getBody().getMessage()));
        }

        CompanyIdDTO companyIdDTO = new CompanyIdDTO(securityServiceCheckCompanySessionResponse.getBody().getUserId());
        HttpEntity<CompanyIdDTO> expeditionServiceGetCompanyExpeditionsRequest = new HttpEntity<>(companyIdDTO, headers);
        ResponseEntity<ExpeditionsForCompanyDTO> expeditionServiceGetCompanyExpeditionsResponse = restTemplate.exchange(
                ServiceURLs.EXPEDITION_SERVICE_GET_COMPANY_EXPEDITIONS_ALL_URL,
                HttpMethod.POST,
                expeditionServiceGetCompanyExpeditionsRequest,
                ExpeditionsForCompanyDTO.class
        );
        if (expeditionServiceGetCompanyExpeditionsResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Getting Company Expeditions Successful (requestId={})", requestId);

        } else if (expeditionServiceGetCompanyExpeditionsResponse.getStatusCode().is4xxClientError()) {
            logger.info("Getting Company Expeditions Failed (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceGetCompanyExpeditionsResponse.getStatusCode())
                    .body(new ExpeditionsForCompanyDTO(expeditionServiceGetCompanyExpeditionsResponse.getBody().getMessage()));

        } else if (expeditionServiceGetCompanyExpeditionsResponse.getStatusCode().is5xxServerError()) {
            logger.info("Getting Company Expeditions Failed at Expedition Service (requestId={})", requestId);
            return ResponseEntity
                    .status(expeditionServiceGetCompanyExpeditionsResponse.getStatusCode())
                    .body(new ExpeditionsForCompanyDTO(expeditionServiceGetCompanyExpeditionsResponse.getBody().getMessage()));
        }

        return ResponseEntity.status(HttpStatus.OK).body(expeditionServiceGetCompanyExpeditionsResponse.getBody());
    }
}