package com.shubilet.security_service.controllers.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;

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
import com.shubilet.security_service.dataTransferObjects.requests.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.LoginDTO;
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;
import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.security_service.mapper.ResponseEntityMapper;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.services.CompanySessionService;
import com.shubilet.security_service.services.CustomerSessionService;

/// TODO: Class yorum satırı eklenecek.
/// TODO: Test edilecek.
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
    public ResponseEntity<MessageDTO> createSession( @RequestBody LoginDTO loginDTO, HttpSession session) {

        if(session == null) {
            logger.warn("Login failed due to missing session");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);

        if( !StringUtils.isNullOrBlank(userId) &&
            !StringUtils.isNullOrBlank(userType) &&
            !StringUtils.isNullOrBlank(authCode)
        ) {
            clearSession(session);
            logger.warn("Login failed: user already logged in with userId {}", userId);
            return ResponseEntity.badRequest().body(ErrorUtils.userAlreadyLoggedIn());
        }
        
        ResponseEntity<CookieDTO> response = null;
        
        if(adminSessionService.hasAdminSession(Integer.parseInt(userId))) {
            response = adminSessionService.createSession(Integer.parseInt(userId));
        }
        else if(companySessionService.hasCompanySession(Integer.parseInt(userId))) {
            response = companySessionService.createSession(Integer.parseInt(userId));
        }
        else if(customerSessionService.hasCustomerSession(Integer.parseInt(userId))) {
            response = customerSessionService.createSession(Integer.parseInt(userId));
        }
        else {
            logger.warn("Login failed for userId {}: user not found", userId);
            return ResponseEntity.badRequest().body(ErrorUtils.notFound("User"));
        }

        if(response == null) {
            logger.warn("Login failed for userId {}: missing response", userId);
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }

        if(response.getStatusCode() == null) {
            logger.warn("Login failed for userId {}: missing response status code", userId);
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }

        if(response.getBody() == null) {
            logger.warn("Login failed for userId {}: missing response body", userId);
            return ResponseEntity.badRequest().body(ErrorUtils.criticalError());
        }

        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Login failed for userId {}: received non-successful status code {}", userId, response.getStatusCode());
            return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.criticalError());
        }

        userId = String.valueOf(response.getBody().getUserId());
        userType = response.getBody().getUserType();
        authCode = response.getBody().getAuthCode();

        session.setAttribute(SessionKeys.USER_ID, userId);
        session.setAttribute(SessionKeys.USER_TYPE, userType);
        session.setAttribute(SessionKeys.AUTH_CODE, authCode);

        logger.info("Login successful for userId {} as {}", userId, userType);
        return ResponseEntity.ok().body(new MessageDTO("Login successful."));
    }

    /**

        Operation: Logout

        Handles the user logout process by validating the current session context, including user
        identifier, user type, and authorization code. Verifies the integrity and format of session
        attributes, delegates logout to the appropriate session service based on user type, and
        clears the HTTP session in both successful and failure scenarios to prevent stale or
        inconsistent authentication state. Returns descriptive error responses when the session is
        missing, invalid, or when the underlying session store does not contain a matching record.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for accessing and clearing authenticated user state</li>
                <li>{@code adminSessionService}, {@code companySessionService}, {@code customerSessionService} for role-specific logout handling</li>
                <li>{@code StringUtils} for null, blank, and numeric checks on session attributes</li>
                <li>{@code ValidationUtils} for validating the authorization code format</li>
                <li>{@code ErrorUtils} for constructing standardized error responses</li>
                <li>{@code SessionKeys} for resolving session attribute names</li>
                <li>{@code UserType} to interpret user type codes stored in the session</li>
                <li>{@code logger} for auditing logout attempts and outcomes</li>
            </ul>
        
        </p>

        @param session the current HTTP session used to retrieve user context and to be cleared on logout

        @return a response entity containing a success message when logout completes correctly,
        or an error payload when the session is invalid or the backing session record cannot be found

    */
    @PostMapping("/logout")
    public ResponseEntity<MessageDTO> logout(HttpSession session) {
        if(session == null) {
            logger.warn("Logout failed due to missing session");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);

        logger.info("Logout attempt for userId {} of type {}", userId, userType);

        if( StringUtils.isNullOrBlank(userId) ||
            StringUtils.isNullOrBlank(userType) ||
            StringUtils.isNullOrBlank(authCode)
        ) {
            clearSession(session);
            logger.warn("Logout failed due to invalid session attributes");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(!ValidationUtils.isValidSessionKey(authCode)) {
            clearSession(session);
            logger.warn("Logout failed due to invalid auth code format");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        } 

        if(!StringUtils.isNumeric(userId)) {
            clearSession(session);
            logger.warn("Logout failed due to non-numeric userId");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(userType.equals(UserType.ADMIN.getCode())) {
            ResponseEntity<Boolean> response = adminSessionService.logout(Integer.parseInt(userId));

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                logger.warn("Logout failed for admin userId {}", userId);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
        }
        else if(userType.equals(UserType.COMPANY.getCode())) {
            ResponseEntity<Boolean> response = companySessionService.logout(Integer.parseInt(userId));

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                logger.warn("Logout failed for company userId {}", userId);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
        }
        else if(userType.equals(UserType.CUSTOMER.getCode())) {
            ResponseEntity<Boolean> response = customerSessionService.logout(Integer.parseInt(userId));

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                logger.warn("Logout failed for customer userId {}", userId);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
        }
        else {
            clearSession(session);
            logger.warn("Logout failed due to invalid user type {}", userType);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        clearSession(session);
        logger.info("Logout successful for userId {} of type {}", userId, userType);
        return ResponseEntity.ok().body(new MessageDTO("Logout successful."));
    }

    /**

        Operation: Validate

        Performs a comprehensive validation of the current HTTP session by checking the presence
        and consistency of user-related session attributes and delegating status verification to
        the appropriate session service based on the stored user type. Ensures the userId, userType,
        and authCode are structurally valid before calling the backend session status endpoint,
        and clears the session when any inconsistency, format issue, or invalid status is detected.
        Returns a success response only when the session is confirmed to be valid and active.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for accessing and managing user session attributes</li>
                <li>{@code adminSessionService}, {@code companySessionService}, {@code customerSessionService} for role-specific session status checks</li>
                <li>{@code validateSessionStatus} to interpret and normalize session status responses</li>
                <li>{@code StringUtils} for null, blank, and numeric validations</li>
                <li>{@code ValidationUtils} for validating the authorization code format</li>
                <li>{@code ErrorUtils} for generating standardized error responses</li>
                <li>{@code SessionKeys} to resolve session attribute keys</li>
                <li>{@code UserType} for interpreting and branching on user type codes</li>
                <li>{@code logger} for diagnostic and audit logging of session validation attempts</li>
            </ul>

        </p>

        @param session the current HTTP session whose attributes and backing status are to be validated

        @return a response entity containing a success message when the session is valid, or
        an error message when the session is missing, malformed, or fails backend validation

    */
    @PostMapping("/check")
    public ResponseEntity<MessageDTO> check(HttpSession session) {
        if(session == null) {
            logger.warn("Session check failed due to missing session");
            return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);

        if( StringUtils.isNullOrBlank(userId) ||
            StringUtils.isNullOrBlank(userType) ||
            StringUtils.isNullOrBlank(authCode)
        ) {
            clearSession(session);
            logger.warn("Session check failed due to invalid session attributes");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(!ValidationUtils.isValidSessionKey(authCode)) {
            clearSession(session);
            logger.warn("Session check failed due to invalid auth code format");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(!StringUtils.isNumeric(userId)) {
            clearSession(session);
            logger.warn("Session check failed due to non-numeric userId");
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
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
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    @PostMapping("/checkAdmin")
    public ResponseEntity<CheckMessageDTO> checkAdminSession(HttpSession session) {
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
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSessionForCheckDTO());
        }

        ResponseEntity<StatusDTO> checkResponse = adminSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.ADMIN);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Admin session check failed for admin userId {}", userId);
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(validationResponse);
        }
        
        logger.info("Admin session check successful for admin userId {}", userId);
        return ResponseEntity.ok().body(new CheckMessageDTO("Session is valid.", Integer.parseInt(userId)));
    }

    @PostMapping("/checkCompany")
    public ResponseEntity<CheckMessageDTO> checkCompanySession(HttpSession session) {
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
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSessionForCheckDTO());
        }

        ResponseEntity<StatusDTO> checkResponse = companySessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.COMPANY);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Company session check failed for company userId {}", userId);
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(validationResponse);
        }

        logger.info("Company session check successful for company userId {}", userId);
        return ResponseEntity.ok().body(new CheckMessageDTO("Session is valid.", Integer.parseInt(userId)));
    }

    @PostMapping("/checkCustomer")
    public ResponseEntity<CheckMessageDTO> checkCustomerSession(HttpSession session) {
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
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSessionForCheckDTO());
        }

        ResponseEntity<StatusDTO> checkResponse = customerSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.CUSTOMER);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Customer session check failed for customer userId {}", userId);
            return ResponseEntityMapper.toCheckMessageDTOResponseEntity(validationResponse);
        }

        logger.info("Customer session check successful for customer userId {}", userId);
        return ResponseEntity.ok().body(new CheckMessageDTO("Session is valid.", Integer.parseInt(userId)));
    }



    ///HELPER METHODS START

    /**
     * Clears the session attributes related to user authentication.
     * @param session   The HttpSession object to clear attributes from.
     */
    private void clearSession(HttpSession session) {
        session.removeAttribute(SessionKeys.USER_ID);
        session.removeAttribute(SessionKeys.USER_TYPE);
        session.removeAttribute(SessionKeys.AUTH_CODE);
        logger.info("Session attributes cleared.");
    }

    /**

        Operation: Validate
        Validates the current HTTP session to ensure that required user-related attributes are
        present and structurally consistent before further processing. Distinguishes between
        completely missing session attributes (treated as a missing session) and partially present
        or malformed attributes, in which case the session is cleared to avoid inconsistent
        authentication state. Verifies that the authorization code and user identifier conform
        to expected formats and returns a success response only when all checks pass.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for reading and clearing user-related session attributes</li>
                <li>{@code StringUtils} for null, blank, and numeric validations</li>
                <li>{@code ValidationUtils} for validating the session authorization code format</li>
                <li>{@code ErrorUtils} for constructing standardized error responses</li>
                <li>{@code SessionKeys} to access userId, userType, and authCode from the session</li>
                <li>{@code clearSession} to reset invalid or inconsistent session state</li>
                <li>{@code logger} for auditing session validation failures and outcomes</li>
            </ul>

        </p>

        @param session the HTTP session whose user-related attributes are to be validated

        @return a response entity with a success message when the session is valid, or an
        error payload when the session is missing, incomplete, or contains invalid data

    */
    private ResponseEntity<MessageDTO> handleValidUserSession(HttpSession session) {
        if(session == null) {
            logger.warn("Session validation failed due to missing session");
            return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
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
                return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
            }
            else {
                clearSession(session);
                logger.warn("Session validation failed due to invalid session attributes");
                return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
            }
        }
        else {

            if(!ValidationUtils.isValidSessionKey(authCode)) {
                clearSession(session);
                logger.warn("Session validation failed due to invalid auth code format");
                return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
            }

            if(!StringUtils.isNumeric(userId)) {
                clearSession(session);
                logger.warn("Session validation failed due to non-numeric userId");
                return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
            }

        }

        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    private ResponseEntity<MessageDTO> validateSessionStatus(ResponseEntity<StatusDTO> response, HttpSession session, UserType userType) {
    
        // Validate response body
        if(response.getBody() == null) {
            clearSession(session);
            logger.warn("Session validation failed due to missing response body");
            return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
        }

        // Get session status
        SessionStatus status = response.getBody().getStatus();

        // Handle non-successful responses
        if(!response.getStatusCode().is2xxSuccessful()) {
            clearSession(session);

            if(status == SessionStatus.NOT_FOUND) {
                logger.warn("Session validation failed: session not found");
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
                
            if(status == SessionStatus.EXPIRED) {
                logger.warn("Session validation failed: session expired");
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionExpired());
            }

            if(status != SessionStatus.VALID) {
                clearSession(session);
                logger.warn("Session validation failed: session not found");
                return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
            }
        }

        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    ///HELPER METHODS END

}   
