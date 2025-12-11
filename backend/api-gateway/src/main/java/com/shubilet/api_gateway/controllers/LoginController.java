package com.shubilet.api_gateway.controllers;


import com.shubilet.api_gateway.common.constants.ServiceURLs;
import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.MemberCredentialsDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.SessionCreationDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.MemberSessionDTO;
import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.mappers.HttpSessionMapper;
import com.shubilet.api_gateway.mappers.MemberSessionMapper;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final RestTemplate restTemplate;
    private final HttpSessionMapper httpSessionMapper;
    private final MemberSessionMapper memberSessionMapper;

    public LoginController(RestTemplate restTemplate, MemberSessionMapper memberSessionMapper) {
        this.restTemplate = restTemplate;
        this.httpSessionMapper = new HttpSessionMapper();
        this.memberSessionMapper = memberSessionMapper;
    }

    @PostMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @PostMapping("")
    public ResponseEntity<MessageDTO> login(HttpSession session, @RequestBody MemberCredentialsDTO memberCredentialsDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Login (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionMapper.toCookieDTO(session);
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

        // Send Request to Member Service For Checking User Credentials
        HttpEntity<MemberCredentialsDTO> memberServiceCredentialCheckRequest = new HttpEntity<>(memberCredentialsDTO, headers);
        ResponseEntity<MemberSessionDTO> memberServiceCredentialCheckResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_CREDENTIALS_CHECK_URL,
                HttpMethod.POST,
                memberServiceCredentialCheckRequest,
                MemberSessionDTO.class
        );

        // User Credentials Validated by Member Service
        if (memberServiceCredentialCheckResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Credentials Check succeeded (requestId={})", requestId);
        }
        // Bad Request
        else if (memberServiceCredentialCheckResponse.getStatusCode().is4xxClientError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceCredentialCheckResponse.getStatusCode(), requestId);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Something went wrong on Member Service
        else if (memberServiceCredentialCheckResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Member service returned error (status={} requestId={})", memberServiceCredentialCheckResponse.getStatusCode(), requestId);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // Convert Member Service Response to DTO For Next Request
        SessionCreationDTO sessionCreationDTO = memberSessionMapper.toSessionCreationDTO(cookieDTO, memberServiceCredentialCheckResponse.getBody());

        // Send Request to Security Service For Creating Session
        HttpEntity<SessionCreationDTO> securityServiceSessionCreationRequest = new HttpEntity<>(sessionCreationDTO, headers);
        ResponseEntity<CookieDTO> securityServiceSessionCreationResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CREATE_SESSION_URL,
                HttpMethod.POST,
                securityServiceSessionCreationRequest,
                CookieDTO.class
        );

        // Session Creation Successful on Security Service
        if (securityServiceSessionCreationResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Session Creation succeeded (requestId={})", requestId);
        }
        //
        else if (securityServiceSessionCreationResponse.getStatusCode().is4xxClientError()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        // Something went wrong on Security Service
        else if (securityServiceSessionCreationResponse.getStatusCode().is5xxServerError()) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        CookieDTO updatedCookieDTO = securityServiceSessionCreationResponse.getBody();
        session.setAttribute("userId", updatedCookieDTO.getUserId());
        session.setAttribute("userType", updatedCookieDTO.getUserType());
        session.setAttribute("authCode", updatedCookieDTO.getAuthCode());

        // Operation Completed Successfully
        return ResponseEntity.status(HttpStatus.OK).body(new MessageDTO("Member Successfully Logged in."));
    }
}