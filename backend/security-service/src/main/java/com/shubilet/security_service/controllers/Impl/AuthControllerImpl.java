package com.shubilet.security_service.controllers.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shubilet.security_service.common.constants.SessionKeys;
import com.shubilet.security_service.common.enums.SessionStatus;
import com.shubilet.security_service.common.enums.UserType;
import com.shubilet.security_service.common.util.ErrorUtils;
import com.shubilet.security_service.common.util.StringUtils;
import com.shubilet.security_service.common.util.ValidationUtils;
import com.shubilet.security_service.controllers.AuthController;
import com.shubilet.security_service.dataTransferObjects.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.LoginDTO;
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;
import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.security_service.mapper.ResponseEntityMapper;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.services.CompanySessionService;
import com.shubilet.security_service.services.CustomerSessionService;

/// TODO: Class yorum satırı eklenecek.
@RestController
@RequestMapping("/api/auth")
//@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.POST})
public class AuthControllerImpl implements AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthControllerImpl.class);

    private final AdminSessionService adminSessionService;
    private final CompanySessionService companySessionService;
    private final CustomerSessionService customerSessionService;

    public AuthControllerImpl(AdminSessionService adminSessionService,
                            CompanySessionService companySessionService,
                            CustomerSessionService customerSessionService
                        ) {
        this.adminSessionService = adminSessionService;
        this.companySessionService = companySessionService;
        this.customerSessionService = customerSessionService;
    }

    /// TODO: method yorum satırı eklenecek.
    @PostMapping("/createSession")
    public ResponseEntity<MessageDTO> createSession(@RequestBody LoginDTO loginDTO) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.MESSAGE_DTO);

        CookieDTO session = loginDTO.getCookie();

        //STEP 1: Classic Validations
        if(session == null) {
            logger.warn("Login failed due to missing session");
            return errorUtils.invalidSession(session);
        }

        String userIdCookie = (String) session.getAttribute(SessionKeys.USER_ID);
        String userTypeCookie = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCodeCookie = (String) session.getAttribute(SessionKeys.AUTH_CODE);

        if( !StringUtils.isNullOrBlank(userIdCookie) &&
            !StringUtils.isNullOrBlank(userTypeCookie) &&
            !StringUtils.isNullOrBlank(authCodeCookie)
        ) {
            clearSession(session);
            logger.warn("Login failed: user already logged in with userId {}", userIdCookie);
            return errorUtils.userAlreadyLoggedIn(session);
        }

        int userId = loginDTO.getUserId();
        String userType = loginDTO.getUserType();

        if(userId <= 0) {
            logger.warn("Login failed due to invalid userId {}", userId);
            return errorUtils.isInvalidFormat(session, String.valueOf(userId));
        }

        if(StringUtils.isNullOrBlank(userType)) {
            logger.warn("Login failed due to missing userType for userId {}", userId);
            return errorUtils.isInvalidFormat(session, "userType");
        }

        //STEP 2: Specific Validations
        
        if(!ValidationUtils.isValidUserType(userType)) {
            logger.warn("Login failed due to invalid userType {} for userId {}", userType, userId);
            return errorUtils.isInvalidFormat(session, "userType");
        }
        
        //STEP 3: Logical Processing

        UserType userTypeEnum = UserType.fromCode(userType);
        ResponseEntity<CookieDTO> response = null;
    
        if(userTypeEnum == UserType.ADMIN) {
            response = adminSessionService.createSession(userId);
        }
        else if(userTypeEnum == UserType.COMPANY) {
            response = companySessionService.createSession(userId);
        }
        else if(userTypeEnum == UserType.CUSTOMER) {
            response = customerSessionService.createSession(userId);
        }
        else {
            logger.warn("Login failed for userId {}: user not found", userId);
            return errorUtils.notFound(session,"User");
        }

        if(response == null) {
            logger.warn("Login failed for userId {}: missing response", userId);
            return errorUtils.criticalError(session);
        }

        if(response.getStatusCode() == null) {
            logger.warn("Login failed for userId {}: missing response status code", userId);
            return errorUtils.criticalError(session);
        }

        if(response.getBody() == null) {
            logger.warn("Login failed for userId {}: missing response body", userId);
            return errorUtils.criticalError(session);
        }

        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Login failed for userId {}: received non-successful status code {}", userId, response.getStatusCode());
            return errorUtils.criticalError(session);
        }

        session.setAttribute(SessionKeys.USER_ID, response.getBody().getUserId());
        session.setAttribute(SessionKeys.USER_TYPE, response.getBody().getUserType());
        session.setAttribute(SessionKeys.AUTH_CODE, response.getBody().getAuthCode());

        logger.info("Login successful for userId {} as {}", userId, userType);
        return ResponseEntity.ok().body(new MessageDTO(session, "Login successful."));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDTO> logout(@RequestBody CookieDTO session) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.MESSAGE_DTO);

        // STEP 1: Classic Validations
        if(session == null) {
            logger.warn("Logout failed due to missing session");
            return errorUtils.invalidSession(session);
        }

        String userId = session.getUserId();
        String userType = session.getUserType();
        String authCode = session.getAuthCode();

        logger.info("Logout attempt for userId {} of type {}", userId, userType);

        if( StringUtils.isNullOrBlank(userId) ||
            StringUtils.isNullOrBlank(userType) ||
            StringUtils.isNullOrBlank(authCode)
        ) {
            clearSession(session);
            logger.warn("Logout failed due to invalid session attributes");
            return errorUtils.invalidSession(session);
        }


        //STEP 2: Specific Validations
        if(!ValidationUtils.isValidSessionKey(authCode)) {
            clearSession(session);
            logger.warn("Logout failed due to invalid auth code format");
            return errorUtils.invalidSession(session);
        } 

        if(!StringUtils.isNumeric(userId)) {
            clearSession(session);
            logger.warn("Logout failed due to non-numeric userId");
            return errorUtils.invalidSession(session);
        }

        //STEP 3: Logical Processing
        if(userType.equals(UserType.ADMIN.getCode())) {
            ResponseEntity<Boolean> response = adminSessionService.logout(Integer.parseInt(userId), authCode);

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                logger.warn("Logout failed for admin userId {}", userId);
                return errorUtils.sessionNotFound(session);
            }
        }
        else if(userType.equals(UserType.COMPANY.getCode())) {
            ResponseEntity<Boolean> response = companySessionService.logout(Integer.parseInt(userId), authCode);

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                logger.warn("Logout failed for company userId {}", userId);
                return errorUtils.sessionNotFound(session);
            }
        }
        else if(userType.equals(UserType.CUSTOMER.getCode())) {
            ResponseEntity<Boolean> response = customerSessionService.logout(Integer.parseInt(userId), authCode);

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                logger.warn("Logout failed for customer userId {}", userId);
                return errorUtils.sessionNotFound(session);
            }
        }
        else {
            clearSession(session);
            logger.warn("Logout failed due to invalid user type {}", userType);
            return errorUtils.invalidSession(session);
        }

        clearSession(session);
        logger.info("Logout successful for userId {} of type {}", userId, userType);
        return ResponseEntity.ok().body(new MessageDTO(session, "Logout successful."));
    }

    @PostMapping("/check")
    public ResponseEntity<MessageDTO> check(@RequestBody CookieDTO session) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.MESSAGE_DTO);

        // STEP 1: Classic Validations
        if(session == null) {
            logger.warn("Session check failed due to missing session");
            return errorUtils.sessionNotFound(session);
        }

        String userId = session.getUserId();
        String userType = session.getUserType();
        String authCode = session.getAuthCode();

        if( StringUtils.isNullOrBlank(userId) ||
            StringUtils.isNullOrBlank(userType) ||
            StringUtils.isNullOrBlank(authCode)
        ) {
            if(StringUtils.isNullOrBlank(userId) &&
                StringUtils.isNullOrBlank(userType) &&
                StringUtils.isNullOrBlank(authCode)
            ) {
                logger.warn("Session not found");
                return errorUtils.sessionNotFound(session);
            }
            else if(StringUtils.isNullOrBlank(userId)) {
                clearSession(session);
                logger.warn("Session check failed due to missing userId {}", userId);
                return errorUtils.invalidSession(session);
            }
            else if(StringUtils.isNullOrBlank(userType)) {
                clearSession(session);
                logger.warn("Session check failed due to missing userType {}", userType);
                return errorUtils.invalidSession(session);
            }
            else if(StringUtils.isNullOrBlank(authCode)) {
                clearSession(session);
                logger.warn("Session check failed due to missing authCode {}", authCode);
                return errorUtils.invalidSession(session);
            }
            else {
                clearSession(session);
                logger.warn("An unexpected error occurred during session check {} {} {}", userId, userType, authCode);
                return errorUtils.criticalError(session);
            }

        }

        if(!ValidationUtils.isValidSessionKey(authCode)) {
            clearSession(session);
            logger.warn("Session check failed due to invalid auth code format");
            return errorUtils.invalidSession(session);
        }

        if(!StringUtils.isNumeric(userId)) {
            clearSession(session);
            logger.warn("Session check failed due to non-numeric userId");
            return errorUtils.invalidSession(session);
        }

        if(userType.equals(UserType.ADMIN.getCode())) {
            ResponseEntity<StatusDTO> response = adminSessionService.check(Integer.parseInt(userId), authCode);

            ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(response, session, UserType.ADMIN);

            if(!validationResponse.getStatusCode().is2xxSuccessful()) {
                logger.warn("Session check failed for admin userId {}", userId);
                return validationResponse;
            }
        }
        else if(userType.equals(UserType.COMPANY.getCode())) {
            ResponseEntity<StatusDTO> response = companySessionService.check(Integer.parseInt(userId), authCode);

            ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(response, session, UserType.COMPANY);

            if(!validationResponse.getStatusCode().is2xxSuccessful()) {
                logger.warn("Session check failed for company userId {}", userId);
                return validationResponse;
            }
        }
        else if(userType.equals(UserType.CUSTOMER.getCode())) {
            ResponseEntity<StatusDTO> response = customerSessionService.check(Integer.parseInt(userId), authCode);

            ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(response, session, UserType.CUSTOMER);

            if(!validationResponse.getStatusCode().is2xxSuccessful()) {
                logger.warn("Session check failed for customer userId {}", userId);
                return validationResponse;
            }
        }
        else {
            clearSession(session);
            return errorUtils.invalidSession(session);
        }

        return ResponseEntity.ok().body(new MessageDTO(session, "Session is valid."));
    }

    @PostMapping("/checkAdmin")
    public ResponseEntity<CheckMessageDTO> checkAdminSession(@RequestBody CookieDTO session) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.CHECK_MESSAGE_DTO);

        // STEP 1: Classic Validations
        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Admin session check failed due to invalid user session");
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(response);
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.ADMIN.getCode())) {
            logger.warn("Admin session check failed due to user type {}", userType);
            return errorUtils.invalidSession(session);
        }

        ResponseEntity<StatusDTO> checkResponse = adminSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.ADMIN);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Admin session check failed for admin userId {}", userId);
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(validationResponse);
        }
        
        logger.info("Admin session check successful for admin userId {}", userId);
        return ResponseEntity.ok().body(new CheckMessageDTO(session, "Session is valid.", Integer.parseInt(userId)));
    }

    @PostMapping("/checkCompany")
    public ResponseEntity<CheckMessageDTO> checkCompanySession(@RequestBody CookieDTO session) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.CHECK_MESSAGE_DTO);

        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Company session check failed due to invalid user session");
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(response);
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.COMPANY.getCode())) {
            logger.warn("Company session check failed due to user type {}", userType);
            return errorUtils.invalidSession(session);
        }

        ResponseEntity<StatusDTO> checkResponse = companySessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.COMPANY);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Company session check failed for company userId {}", userId);
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(validationResponse);
        }

        logger.info("Company session check successful for company userId {}", userId);
        return ResponseEntity.ok().body(new CheckMessageDTO(session, "Session is valid.", Integer.parseInt(userId)));
    }

    @PostMapping("/checkCustomer")
    public ResponseEntity<CheckMessageDTO> checkCustomerSession(@RequestBody CookieDTO session) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.CHECK_MESSAGE_DTO);

        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Customer session check failed due to invalid user session");
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(response);
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.CUSTOMER.getCode())) {
            logger.warn("Customer session check failed due to user type {}", userType);
            return errorUtils.invalidSession(session);
        }

        ResponseEntity<StatusDTO> checkResponse = customerSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.CUSTOMER);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Customer session check failed for customer userId {}", userId);
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(validationResponse);
        }

        logger.info("Customer session check successful for customer userId {}", userId);
        return ResponseEntity.ok().body(new CheckMessageDTO(session, "Session is valid.", Integer.parseInt(userId)));
    }



    ///HELPER METHODS START

    private void clearSession(CookieDTO session) {
        session.removeAttribute(SessionKeys.USER_ID);
        session.removeAttribute(SessionKeys.USER_TYPE);
        session.removeAttribute(SessionKeys.AUTH_CODE);
        logger.info("Session attributes cleared.");
    }

    private ResponseEntity<MessageDTO> handleValidUserSession(CookieDTO session) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.MESSAGE_DTO);
        if(session == null) {
            logger.warn("Session validation failed due to missing session");
            return errorUtils.sessionNotFound(session);
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);

        if( StringUtils.isNullOrBlank(userId) ||
            StringUtils.isNullOrBlank(userType) ||
            StringUtils.isNullOrBlank(authCode)
        ) {
            if(StringUtils.isNullOrBlank(userId) &&
                StringUtils.isNullOrBlank(userType) &&
                StringUtils.isNullOrBlank(authCode)
            ) {
                logger.warn("Session validation failed due to missing session attributes");
                return errorUtils.sessionNotFound(session);
            }
            else {
                clearSession(session);
                logger.warn("Session validation failed due to invalid session attributes");
                return errorUtils.invalidSession(session);
            }
        }
        else {

            if(!ValidationUtils.isValidSessionKey(authCode)) {
                clearSession(session);
                logger.warn("Session validation failed due to invalid auth code format");
                return errorUtils.invalidSession(session);
            }

            if(!StringUtils.isNumeric(userId)) {
                clearSession(session);
                logger.warn("Session validation failed due to non-numeric userId");
                return errorUtils.invalidSession(session);
            }

        }

        return ResponseEntity.ok().body(new MessageDTO(session, "Session is valid."));
    }

    private ResponseEntity<MessageDTO> validateSessionStatus(ResponseEntity<StatusDTO> response, CookieDTO session, UserType userType) {
        ErrorUtils errorUtils = new ErrorUtils(ErrorUtils.ConversionType.MESSAGE_DTO);
    
        // Validate response body
        if(response.getBody() == null) {
            clearSession(session);
            logger.warn("Session validation failed due to missing response body");
            return errorUtils.sessionNotFound(session);
        }

        // Get session status
        SessionStatus status = response.getBody().getStatus();

        // Handle non-successful responses
        if(!response.getStatusCode().is2xxSuccessful()) {
            clearSession(session);

            if(status == SessionStatus.NOT_FOUND) {
                logger.warn("Session validation failed: session not found");
                return errorUtils.sessionNotFound(session);
            }
                
            if(status == SessionStatus.EXPIRED) {
                logger.warn("Session validation failed: session expired");
                return errorUtils.sessionExpired(session);
            }

            if(status != SessionStatus.VALID) {
                clearSession(session);
                logger.warn("Session validation failed: session not found");
                return errorUtils.sessionNotFound(session);
            }
        }

        return ResponseEntity.ok().body(new MessageDTO(session, "Session is valid."));
    }

    ///HELPER METHODS END

}   
