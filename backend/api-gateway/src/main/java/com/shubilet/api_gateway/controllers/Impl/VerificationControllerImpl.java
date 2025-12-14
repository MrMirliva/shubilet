package com.shubilet.api_gateway.controllers.Impl;

import com.shubilet.api_gateway.common.constants.ServiceURLs;
import com.shubilet.api_gateway.controllers.VerificationController;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.verification.CompanyVerificationInternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.MessageDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.VerificationController.AdminVerificationExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.external.requests.VerificationController.CompanyVerificationExternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.verification.AdminVerificationInternalDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.auth.MemberCheckMessageDTO;
import com.shubilet.api_gateway.managers.HttpSessionManager;
import com.shubilet.api_gateway.mappers.AdminVerificationExternalMapper;
import com.shubilet.api_gateway.mappers.CompanyVerificationExternalMapper;
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
@RequestMapping("api/auth/verify")
public class VerificationControllerImpl implements VerificationController {
    private final Logger logger = LoggerFactory.getLogger(VerificationControllerImpl.class);
    private final RestTemplate restTemplate;
    private final HttpSessionManager httpSessionManager;
    private final CompanyVerificationExternalMapper companyVerificationExternalMapper;
    private final AdminVerificationExternalMapper adminVerificationExternalMapper;

    public VerificationControllerImpl(RestTemplate restTemplate,
                                      CompanyVerificationExternalMapper companyVerificationExternalMapper,
                                      AdminVerificationExternalMapper adminVerificationExternalMapper) {
        this.restTemplate = restTemplate;
        this.companyVerificationExternalMapper = companyVerificationExternalMapper;
        this.adminVerificationExternalMapper = adminVerificationExternalMapper;
        this.httpSessionManager = new HttpSessionManager();
    }


    @PostMapping("/company")
    public ResponseEntity<MessageDTO> verifyCompany(HttpSession httpSession, @RequestBody CompanyVerificationExternalDTO companyVerificationExternalDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Company Verification (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionCustomerRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckCustomerSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_ADMIN_SESSION_URL,
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
            return ResponseEntity
                    .status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new MessageDTO("There is no Existing Admin Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckCustomerSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity
                    .status(securityServiceCheckCustomerSessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckCustomerSessionResponse.getBody().getMessage()));
        }
        CompanyVerificationInternalDTO companyVerificationInternalDTO = companyVerificationExternalMapper.toCompanyVerificationInternalDTO(
                companyVerificationExternalDTO,
                securityServiceCheckCustomerSessionResponse.getBody()
        );

        HttpEntity<CompanyVerificationInternalDTO> memberServiceCompanyVerificationRequest = new HttpEntity<>(companyVerificationInternalDTO, headers);
        ResponseEntity<MessageDTO> memberServiceCompanyVerificationResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_VERIFY_COMPANY_URL,
                HttpMethod.POST,
                memberServiceCompanyVerificationRequest,
                MessageDTO.class
        );
        if (memberServiceCompanyVerificationResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Company Verified Successfully (requestId={})", requestId);
        } else if (memberServiceCompanyVerificationResponse.getStatusCode().is4xxClientError()) {
            logger.warn("Bad Request to MemberService(requestId={})", requestId);
            return ResponseEntity
                    .status(memberServiceCompanyVerificationResponse.getStatusCode())
                    .body(memberServiceCompanyVerificationResponse.getBody());
        } else if (memberServiceCompanyVerificationResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Internal Server Error at MemberService(requestId={})", requestId);
            return ResponseEntity
                    .status(memberServiceCompanyVerificationResponse.getStatusCode())
                    .body(memberServiceCompanyVerificationResponse.getBody());
        }

        return ResponseEntity.ok().body(memberServiceCompanyVerificationResponse.getBody());
    }

    @PostMapping("/admin")
    public ResponseEntity<MessageDTO> verifyAdmin(HttpSession httpSession, @RequestBody AdminVerificationExternalDTO adminVerificationExternalDTO) {
        String requestId = UUID.randomUUID().toString();
        logger.info("Start Admin Verification (requestId={})", requestId);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", requestId);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Send Request to Security Service for Checking Existing Session
        CookieDTO cookieDTO = httpSessionManager.fromSessionToCookieDTO(httpSession);
        HttpEntity<CookieDTO> securityServiceCheckSessionAdminRequest = new HttpEntity<>(cookieDTO, headers);
        ResponseEntity<MemberCheckMessageDTO> securityServiceCheckAdminSessionResponse = restTemplate.exchange(
                ServiceURLs.SECURITY_SERVICE_CHECK_ADMIN_SESSION_URL,
                HttpMethod.POST,
                securityServiceCheckSessionAdminRequest,
                MemberCheckMessageDTO.class
        );

        // There is already an Existing Session
        if (securityServiceCheckAdminSessionResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Customer Session Exists (requestId={})", requestId);
        }

        // No User is Logged in Clarified by Security Service
        if (securityServiceCheckAdminSessionResponse.getStatusCode().is4xxClientError()) {
            return ResponseEntity
                    .status(securityServiceCheckAdminSessionResponse.getStatusCode())
                    .body(new MessageDTO("There is no Existing Admin Session."));
        }

        // Something Went Wrong on Security Service
        if (securityServiceCheckAdminSessionResponse.getStatusCode().is5xxServerError()) {
            return ResponseEntity
                    .status(securityServiceCheckAdminSessionResponse.getStatusCode())
                    .body(new MessageDTO(securityServiceCheckAdminSessionResponse.getBody().getMessage()));
        }

        AdminVerificationInternalDTO adminVerificationInternalDTO = adminVerificationExternalMapper.toAdminVerificationInternalDTO(
                adminVerificationExternalDTO,
                securityServiceCheckAdminSessionResponse.getBody()
        );

        HttpEntity<AdminVerificationInternalDTO> memberServiceAdminVerificationRequest = new HttpEntity<>(adminVerificationInternalDTO, headers);
        ResponseEntity<MessageDTO> memberServiceAdminVerificationResponse = restTemplate.exchange(
                ServiceURLs.MEMBER_SERVICE_VERIFY_ADMIN_URL,
                HttpMethod.POST,
                memberServiceAdminVerificationRequest,
                MessageDTO.class
        );
        if (memberServiceAdminVerificationResponse.getStatusCode().is2xxSuccessful()) {
            logger.info("Admin Verified Successfully (requestId={})", requestId);
        } else if (memberServiceAdminVerificationResponse.getStatusCode().is4xxClientError()) {
            logger.warn("Bad Request to Member Service (requestId={})", requestId);
            return ResponseEntity
                    .status(memberServiceAdminVerificationResponse.getStatusCode())
                    .body(memberServiceAdminVerificationResponse.getBody());
        } else if (memberServiceAdminVerificationResponse.getStatusCode().is5xxServerError()) {
            logger.warn("Internal Server Error at Member Service(requestId={})", requestId);
            return ResponseEntity
                    .status(memberServiceAdminVerificationResponse.getStatusCode())
                    .body(memberServiceAdminVerificationResponse.getBody());
        }
        return ResponseEntity.ok().body(memberServiceAdminVerificationResponse.getBody());
    }
}
