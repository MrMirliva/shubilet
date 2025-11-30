package com.shubilet.security_service.controllers.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

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
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.services.CompanySessionService;
import com.shubilet.security_service.services.CustomerSessionService;

/**

    Domain: Authentication

    Implements the authentication and session management REST API for the security service.
    This controller exposes endpoints for login, logout, and session validation for different
    user roles (admin, company, and customer), coordinating between HTTP session state and
    role-specific backend session services. It centralizes session consistency checks,
    error mapping, and logging to provide a unified entry point for session-based security
    operations in the application. As the concrete implementation of {@link AuthController},
    it acts as the boundary between external HTTP requests and the internal authentication
    and session lifecycle logic.

    <p>

        Technologies:

        <ul>
            <li>Spring Web (REST controller and request mapping)</li>
            <li>Jakarta Servlet {@code HttpSession} for server-side session management</li>
            <li>SLF4J for structured application logging</li>
        </ul>

    </p>

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMirliva

    @see com.shubilet.security_service.controllers.AuthController

    @see com.shubilet.security_service.services.AdminSessionService

    @see com.shubilet.security_service.services.CompanySessionService

    @see com.shubilet.security_service.services.CustomerSessionService

    @version 1.0
*/
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


    /**

        Operation: Login

        Handles the user login workflow by validating credentials, determining the user type
        (admin, company, or customer), delegating authentication to the appropriate session
        service, and updating the HTTP session with authenticated user context. Produces
        detailed error responses for invalid input, unverified accounts, incorrect passwords,
        already logged-in users, and missing or inconsistent session information.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for tracking authenticated user state</li>
                <li>{@code adminSessionService}, {@code companySessionService}, and {@code customerSessionService} for role-specific authentication</li>
                <li>{@code ValidationUtils} and {@code StringUtils} for input and format validation</li>
                <li>{@code ErrorUtils} for building standardized error response payloads</li>
                <li>{@code SessionKeys} to read and write user-related session attributes</li>
                <li>{@code UserType} to describe the type of authenticated user</li>
                <li>{@code logger} for audit and diagnostics of login attempts and outcomes</li>
            </ul>
        </p>

        @param loginDTO the login payload containing email and password credentials

        @param session the current HTTP session used to check and store authentication state

        @return a response entity containing either a success message when login is successful 
        or a descriptive error message when validation or authentication fails

    */
    @PostMapping("/login")
    public ResponseEntity<MessageDTO> login( @RequestBody LoginDTO loginDTO, HttpSession session) {

        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        logger.info("Login attempt for email {}", email);

        if(session == null) {
            logger.warn("Login failed for email {} due to missing session", email);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(StringUtils.isNullOrBlank(email)) {
            logger.warn("Login failed due to missing email");
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Email"));
        }

        if(StringUtils.isNullOrBlank(password)) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Password"));
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);

        if( !StringUtils.isNullOrBlank(userId) &&
            !StringUtils.isNullOrBlank(userType) &&
            !StringUtils.isNullOrBlank(authCode)
        ) {
            clearSession(session);
            logger.warn("Login blocked for email {} because a user is already logged in", email);
            return ResponseEntity.badRequest().body(ErrorUtils.userAlreadyLoggedIn());
        }

        if( !ValidationUtils.isValidEmail(email) ) {
            logger.warn("Login failed for email {} due to invalid email format", email);
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Email"));
        }

        if( !ValidationUtils.isValidPassword(password) ) {
            logger.warn("Login failed for email {} due to invalid password format", email);
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Password"));
        }

        if(adminSessionService.hasEmail(email)) {
            if(adminSessionService.isVerifiedEmail(email)) {
                ResponseEntity<CookieDTO> response = adminSessionService.login(email, password);

                if(response.getStatusCode().is2xxSuccessful()) {
                    CookieDTO sessionInfo = response.getBody();
                    if(sessionInfo != null) {
                        userId = String.valueOf(sessionInfo.getUserId());
                        userType = sessionInfo.getUserType();
                        authCode = sessionInfo.getAuthCode();
                    }
                    else {
                        logger.warn("Login failed for email {}: missing session info in response", email);
                        return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.criticalError());
                    }
                }
                else {
                    logger.warn("Login failed for email {}: incorrect admin password", email);
                    return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.isIncorrect("Password"));
                }
            }
            else {
                logger.warn("Login failed for email {}: admin email not verified", email);
                return ResponseEntity.badRequest().body(ErrorUtils.isNotVerified(UserType.ADMIN));
            }
        }
        else if(companySessionService.hasEmail(email)) {
            if(companySessionService.isVerifiedEmail(email)) {
                ResponseEntity<CookieDTO> response = companySessionService.login(email, password);

                if(response.getStatusCode().is2xxSuccessful()) {
                    CookieDTO sessionInfo = response.getBody();
                    if(sessionInfo != null) {
                        userId = String.valueOf(sessionInfo.getUserId());
                        userType = sessionInfo.getUserType();
                        authCode = sessionInfo.getAuthCode();
                    }
                    else {
                        logger.warn("Login failed for email {}: missing session info in response", email);
                        return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.criticalError());
                    }
                }
                else {
                    logger.warn("Login failed for email {}: incorrect company password", email);
                    return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.isIncorrect("Password"));
                }
            }
            else {
                logger.warn("Login failed for email {}: company email not verified", email);
                return ResponseEntity.badRequest().body(ErrorUtils.isNotVerified(UserType.COMPANY));
            }
        }
        else if(customerSessionService.hasEmail(email)) {
            ResponseEntity<CookieDTO> response = customerSessionService.login(email, password);

            if(response.getStatusCode().is2xxSuccessful()) {
                CookieDTO sessionInfo = response.getBody();
                if(sessionInfo != null) {
                    userId = String.valueOf(sessionInfo.getUserId());
                    userType = sessionInfo.getUserType();
                    authCode = sessionInfo.getAuthCode();
                }
                else {
                    logger.warn("Login failed for email {}: missing session info in response", email);
                    return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.criticalError());
                }
            }
            else {
                logger.warn("Login failed for email {}: incorrect customer password", email);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.isIncorrect("Password"));
            }
        }
        else {
            logger.warn("Login failed for email {}: email not found", email);
            return ResponseEntity.badRequest().body(ErrorUtils.notFound("Email"));
        }


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

    /**

        Operation: Validate

        Performs a dedicated validation of the current admin user session by first ensuring that
        a generic user session is structurally valid and then enforcing that the session belongs
        to an administrator. Delegates the final status check to the admin session service using
        the userId and authCode stored in the HTTP session, and returns an error when the user
        type is not admin, the session is invalid, or the backend status check fails. Produces
        a success response only when the session is confirmed to belong to a valid and active
        admin user.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for accessing and validating admin-related session attributes</li>
                <li>{@code handleValidUserSession} to perform generic user session validation</li>
                <li>{@code adminSessionService} for admin-specific session status checks</li>
                <li>{@code validateSessionStatus} to normalize and interpret session status responses</li>
                <li>{@code SessionKeys} to read userId, userType, and authCode from the session</li>
                <li>{@code UserType} to verify that the current user is an administrator</li>
                <li>{@code ErrorUtils} for building standardized error responses</li>
                <li>{@code logger} for auditing admin session validation attempts and outcomes</li>
            </ul>

        </p>

        @param session the current HTTP session expected to contain an authenticated admin context

        @return a response entity containing a success message when the admin session is valid,
        or an error message when the session is missing, invalid, non-admin, or fails
        backend validation


    */
    @PostMapping("/checkAdmin")
    public ResponseEntity<MessageDTO> checkAdminSession(HttpSession session) {
        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Admin session check failed due to invalid user session");
            return response;
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.ADMIN.getCode())) {
            logger.warn("Admin session check failed due to user type {}", userType);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        ResponseEntity<StatusDTO> checkResponse = adminSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.ADMIN);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Admin session check failed for admin userId {}", userId);
            return validationResponse;
        }
        
        logger.info("Admin session check successful for admin userId {}", userId);
        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    /**

        Operation: Validate

        Performs a dedicated validation of the current company user session by first invoking a
        generic user session validation and then enforcing that the resolved user type is company.
        Delegates the final status check to the company session service using the userId and
        authCode stored in the HTTP session, and returns an error when the user type is not company,
        the session is invalid, or the backend status check fails. Produces a success response only
        when the session is confirmed to belong to a valid and active company user.

        <p>

        Uses:

            <ul>
                <li>{@code HttpSession} for accessing and validating company-related session attributes</li>
                <li>{@code handleValidUserSession} to perform generic user session validation</li>
                <li>{@code companySessionService} for company-specific session status checks</li>
                <li>{@code validateSessionStatus} to normalize and interpret session status responses</li>
                <li>{@code SessionKeys} to read userId, userType, and authCode from the session</li>
                <li>{@code UserType} to verify that the current user is a company account</li>
                <li>{@code ErrorUtils} for constructing standardized error responses</li>
                <li>{@code logger} for auditing company session validation attempts and outcomes</li>
            </ul>

        </p>

        @param session the current HTTP session expected to contain an authenticated company context

        @return a response entity containing a success message when the company session is valid,
        or an error message when the session is missing, invalid, non-company, or fails
        backend validation


    */
    @PostMapping("/checkCompany")
    public ResponseEntity<MessageDTO> checkCompanySession(HttpSession session) {
        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Company session check failed due to invalid user session");
            return response;
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.COMPANY.getCode())) {
            logger.warn("Company session check failed due to user type {}", userType);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        ResponseEntity<StatusDTO> checkResponse = companySessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.COMPANY);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Company session check failed for company userId {}", userId);
            return validationResponse;
        }

        logger.info("Company session check successful for company userId {}", userId);
        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    /**

        Operation: Validate

        Performs a dedicated validation of the current customer user session by first executing
        a generic session validation routine and then ensuring that the resolved user type
        corresponds to a customer account. Delegates the final status verification to the
        customer session service using the userId and authCode stored in the HTTP session,
        and returns an error when the user type is not customer, the session is invalid, or the
        backend session-status check fails. Returns a success response only when the session is
        confirmed to belong to a valid and active customer.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for retrieving and validating customer-related session attributes</li>
                <li>{@code handleValidUserSession} for generic session validation across all user types</li>
                <li>{@code customerSessionService} for customer-specific session status verification</li>
                <li>{@code validateSessionStatus} to interpret and normalize the backend session-status response</li>
                <li>{@code SessionKeys} for reading userId, userType, and authCode from the session</li>
                <li>{@code UserType} for checking whether the current user is a customer</li>
                <li>{@code ErrorUtils} for generating standardized error responses</li>
                <li>{@code logger} for auditing customer session validation attempts and outcomes</li>
            </ul>

        </p>

        @param session the current HTTP session expected to contain an authenticated customer context

        @return a response entity containing a success message when the customer session is valid,
        or an error message when the session is missing, invalid, non-customer, or fails
        backend validation

    */
    @PostMapping("/checkCustomer")
    public ResponseEntity<MessageDTO> checkCustomerSession(HttpSession session) {
        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Customer session check failed due to invalid user session");
            return response;
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.CUSTOMER.getCode())) {
            logger.warn("Customer session check failed due to user type {}", userType);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        ResponseEntity<StatusDTO> checkResponse = customerSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.CUSTOMER);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            logger.warn("Customer session check failed for customer userId {}", userId);
            return validationResponse;
        }

        logger.info("Customer session check successful for customer userId {}", userId);
        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
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


    /**

        Operation: Normalize

        Interprets and normalizes the result of a backend session status check by evaluating the
        HTTP status, validating the presence of the response body, and mapping specific
        {@link SessionStatus} values to standardized error responses. Clears the HTTP session
        whenever an invalid, expired, not found, or otherwise inconsistent session state is
        detected, and applies special rules for unverified sessions depending on the provided
        {@link UserType}. Returns a success response only when the backend response is successful
        and the session status is considered valid.

        <p>

            Uses:

            <ul>
                <li>{@code ResponseEntity<StatusDTO>} to obtain the backend session status and HTTP code</li>
                <li>{@code HttpSession} for clearing invalid or inconsistent session state</li>
                <li>{@code SessionStatus} to interpret session validity, expiration, and verification state</li>
                <li>{@code UserType} to apply user-type-specific rules for unverified sessions</li>
                <li>{@code ErrorUtils} for constructing standardized error responses for each failure case</li>
                <li>{@code clearSession} to reset session data on error conditions</li>
                <li>{@code logger} for auditing session validation outcomes and anomalies</li>
            </ul>

        </p>

        @param response the backend response containing session status and HTTP status code

        @param session the HTTP session to be cleared when the session is invalid or inconsistent

        @param userType the type of user whose session status is being validated

        @return a response entity containing a success message when the session is valid, or an
        error payload reflecting not found, expired, unverified, or otherwise invalid session states

    */
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

            if(status == SessionStatus.NOT_VERIFIED) {
                if(userType == UserType.CUSTOMER) {
                    logger.warn("Session validation failed: customer sessions cannot be unverified");
                    return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.criticalError());
                }
                logger.warn("Session validation failed: {} not verified", userType.getCode());
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotVerified(userType));
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
