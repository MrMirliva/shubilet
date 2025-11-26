package com.shubilet.security_service.controllers.Impl;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.common.constants.SessionKeys;
import com.shubilet.security_service.common.enums.UserType;
import com.shubilet.security_service.common.util.ErrorUtils;
import com.shubilet.security_service.common.util.StringUtils;
import com.shubilet.security_service.common.util.ValidationUtils;
import com.shubilet.security_service.controllers.AuthController;
import com.shubilet.security_service.dataTransferObjects.requests.SessionInfoDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.services.CompanySessionService;
import com.shubilet.security_service.services.CustomerSessionService;

import jakarta.servlet.http.HttpSession;


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
    ///TODO: Süresi geçmiş oturum kontrolü eklenecek 
    
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
                ResponseEntity<SessionInfoDTO> response = adminSessionService.login(email, password);

                if(response.getStatusCode().is2xxSuccessful()) {
                    SessionInfoDTO sessionInfo = response.getBody();
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
                ResponseEntity<SessionInfoDTO> response = companySessionService.login(email, password);

                if(response.getStatusCode().is2xxSuccessful()) {
                    SessionInfoDTO sessionInfo = response.getBody();
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
            ResponseEntity<SessionInfoDTO> response = customerSessionService.login(email, password);

            if(response.getStatusCode().is2xxSuccessful()) {
                SessionInfoDTO sessionInfo = response.getBody();
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
            ResponseEntity<Boolean> response = adminSessionService.check(Integer.parseInt(userId), authCode);

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                clearSession(session);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }

            if(!response.getBody()) {
                clearSession(session);
                return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
            }
        }
        else if(userType.equals(UserType.COMPANY.getCode())) {
            ResponseEntity<Boolean> response = companySessionService.check(Integer.parseInt(userId), authCode);

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                clearSession(session);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }

            if(!response.getBody()) {
                clearSession(session);
                return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
            }
        }
        else if(userType.equals(UserType.CUSTOMER.getCode())) {
            ResponseEntity<Boolean> response = customerSessionService.check(Integer.parseInt(userId), authCode);

            if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                clearSession(session);
                return ResponseEntity.status(response.getStatusCode()).body(ErrorUtils.sessionNotFound());
            }

            if(!response.getBody()) {
                clearSession(session);
                return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
            }
        }
        else {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.invalidSession());
        }

        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

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

        if( adminSessionService.check(
                Integer.parseInt(userId),
                authCode
            ).getBody() != Boolean.TRUE
        ) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
        }
        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

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

        if( companySessionService.check(
                Integer.parseInt(userId),
                authCode
            ).getBody() != Boolean.TRUE
        ) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
        }
        return ResponseEntity.ok().body(new MessageDTO("Session is valid."));
    }

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

        if( customerSessionService.check(
                Integer.parseInt(userId),
                authCode
            ).getBody() != Boolean.TRUE
        ) {
            clearSession(session);
            return ResponseEntity.badRequest().body(ErrorUtils.sessionNotFound());
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

    ///HELPER METHODS END

}   
