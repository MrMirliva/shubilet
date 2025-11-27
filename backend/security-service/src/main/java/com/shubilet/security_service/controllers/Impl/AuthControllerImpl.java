package com.shubilet.security_service.controllers.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.services.CompanySessionService;
import com.shubilet.security_service.services.CustomerSessionService;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.POST})
public class AuthControllerImpl implements AuthController {
    private final AdminSessionService adminSessionService;
    private final CompanySessionService companySessionService;
    private final CustomerSessionService customerSessionService;

    public AuthControllerImpl(AdminSessionService adminSessionService,
                            CompanySessionService companySessionService,
                            CustomerSessionService customerSessionService,
                            StringUtils stringUtils
                        ) {
        this.adminSessionService = adminSessionService;
        this.companySessionService = companySessionService;
        this.customerSessionService = customerSessionService;
    }

    ///TODO: Yorum satırları eklenecek
    ///TODO: Loglama eklenecek
    
    @PostMapping("/login")
    public ResponseEntity<MessageDTO> login(String email, String password, HttpSession session) {
        if(session == null) {
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(StringUtils.isNullOrBlank(email)) {
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
            return ResponseEntity.badRequest().body(ErrorUtils.userAlreadyLoggedIn());
        }

        if( !ValidationUtils.isValidEmail(email) ) {
            return ResponseEntity.badRequest().body(ErrorUtils.isInvalidFormat("Email"));
        }

        if( !ValidationUtils.isValidPassword(password) ) {
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
                }
                else {
                    return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.isIncorrect("Password"));
                }
            }
            else {
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
                }
                else {
                    return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.isIncorrect("Password"));
                }
            }
            else {
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
            }
            else {
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.isIncorrect("Password"));
            }
        }
        else {
            return ResponseEntity.badRequest().body(ErrorUtils.notFound("Email"));
        }


        session.setAttribute(SessionKeys.USER_ID, userId);
        session.setAttribute(SessionKeys.USER_TYPE, userType);
        session.setAttribute(SessionKeys.AUTH_CODE, authCode);
        return ResponseEntity.ok().body(new MessageDTO("Login successful."));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageDTO> logout(HttpSession session) {
        if(session == null) {
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);

        if( StringUtils.isNullOrBlank(userId) ||
            StringUtils.isNullOrBlank(userType) ||
            StringUtils.isNullOrBlank(authCode)
        ) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(!ValidationUtils.isValidSessionKey(authCode)) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        } 

        if(!StringUtils.isNumeric(userId)) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(userType.equals(UserType.ADMIN.getCode())) {
            ResponseEntity<Boolean> response = adminSessionService.logout(Integer.parseInt(userId));

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
        }
        else if(userType.equals(UserType.COMPANY.getCode())) {
            ResponseEntity<Boolean> response = companySessionService.logout(Integer.parseInt(userId));

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
        }
        else if(userType.equals(UserType.CUSTOMER.getCode())) {
            ResponseEntity<Boolean> response = customerSessionService.logout(Integer.parseInt(userId));

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || !response.getBody()) {
                clearSession(session);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
        }
        else {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        clearSession(session);
        return ResponseEntity.ok().body(new MessageDTO("Logout successful."));
    }

    @PostMapping("/check")
    public ResponseEntity<MessageDTO> check(HttpSession session) {
        if(session == null) {
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
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(!ValidationUtils.isValidSessionKey(authCode)) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(!StringUtils.isNumeric(userId)) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        if(userType.equals(UserType.ADMIN.getCode())) {
            ResponseEntity<StatusDTO> response = adminSessionService.check(Integer.parseInt(userId), authCode);

            ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(response, session, UserType.ADMIN);

            if(!validationResponse.getStatusCode().is2xxSuccessful()) {
                return validationResponse;
            }
        }
        else if(userType.equals(UserType.COMPANY.getCode())) {
            ResponseEntity<StatusDTO> response = companySessionService.check(Integer.parseInt(userId), authCode);

            ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(response, session, UserType.COMPANY);

            if(!validationResponse.getStatusCode().is2xxSuccessful()) {
                return validationResponse;
            }
        }
        else if(userType.equals(UserType.CUSTOMER.getCode())) {
            ResponseEntity<StatusDTO> response = customerSessionService.check(Integer.parseInt(userId), authCode);

            ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(response, session, UserType.CUSTOMER);

            if(!validationResponse.getStatusCode().is2xxSuccessful()) {
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
    public ResponseEntity<MessageDTO> checkAdminSession(HttpSession session) {
        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.ADMIN.getCode())) {
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        ResponseEntity<StatusDTO> checkResponse = adminSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.ADMIN);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }
        
        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    @PostMapping("/checkCompany")
    public ResponseEntity<MessageDTO> checkCompanySession(HttpSession session) {
        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.COMPANY.getCode())) {
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        ResponseEntity<StatusDTO> checkResponse = companySessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.COMPANY);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    @PostMapping("/checkCustomer")
    public ResponseEntity<MessageDTO> checkCustomerSession(HttpSession session) {
        ResponseEntity<MessageDTO> response = handleValidUserSession(session);
        if(!response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        String userId = (String) session.getAttribute(SessionKeys.USER_ID);
        String userType = (String) session.getAttribute(SessionKeys.USER_TYPE);
        String authCode = (String) session.getAttribute(SessionKeys.AUTH_CODE);
        
        if(!userType.equals(UserType.CUSTOMER.getCode())) {
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        ResponseEntity<StatusDTO> checkResponse = customerSessionService.check(Integer.parseInt(userId), authCode);

        ResponseEntity<MessageDTO> validationResponse = validateSessionStatus(checkResponse, session, UserType.CUSTOMER);

        if(!validationResponse.getStatusCode().is2xxSuccessful()) {
            return validationResponse;
        }

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
    }

    /**
     * Handles validation of a user session.
     * @param session  The HttpSession object to validate.
     * @return ResponseEntity containing a MessageDTO indicating the result of the validation.
     */
    private ResponseEntity<MessageDTO> handleValidUserSession(HttpSession session) {
        if(session == null) {
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
                return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
            }
            else {
                clearSession(session);
                return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
            }
        }
        else {

            if(!ValidationUtils.isValidSessionKey(authCode)) {
                clearSession(session);
                return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
            }

            if(!StringUtils.isNumeric(userId)) {
                clearSession(session);
                return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
            }

        }

        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }


    /**
     * Validates the session status based on the response from the session service.
     * @param response The ResponseEntity containing the StatusDTO from the session service.
     * @param session  The HttpSession object to potentially clear.
     * @param userType The type of user (ADMIN, COMPANY, CUSTOMER).
     * @return ResponseEntity containing a MessageDTO indicating the result of the validation.
     */
    private ResponseEntity<MessageDTO> validateSessionStatus(ResponseEntity<StatusDTO> response, HttpSession session, UserType userType) {
    
        // Validate response body
        if(response.getBody() == null) {
            clearSession(session);
            return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
        }

        // Get session status
        SessionStatus status = response.getBody().getStatus();

        // Handle non-successful responses
        if(!response.getStatusCode().is2xxSuccessful()) {
            clearSession(session);

            if(status == SessionStatus.NOT_FOUND) {
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }
                
            if(status == SessionStatus.EXPIRED) {
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionExpired());
            }

            if(status == SessionStatus.NOT_VERIFIED) {
                if(userType == UserType.CUSTOMER) {
                    return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.criticalError());
                }
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotVerified(userType));
            }

            if(status != SessionStatus.VALID) {
                clearSession(session);
                return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
            }
        }

        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

    ///HELPER METHODS END

}   
