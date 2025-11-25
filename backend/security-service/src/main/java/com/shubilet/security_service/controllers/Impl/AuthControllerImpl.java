package com.shubilet.security_service.controllers.Impl;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.common.constants.SessionKeys;
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
    public ResponseEntity<MessageDTO> login(String email, String password, HttpSession session) {
        if(email == null) {
            return ResponseEntity.badRequest().body(ErrorUtils.isNull("Email"));
        }

        if(password == null) {
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

        if(adminSessionService.hasEmail(email)) { ///TODO: onaylı mı değil mi kontrolü eklenecek
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
        else if(companySessionService.hasEmail(email)) { ///TODO: onaylı mı değil mi kontrolü eklenecek
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
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseEntity<MessageDTO> check(HttpSession session) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseEntity<MessageDTO> checkAdminSession(HttpSession session) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseEntity<MessageDTO> checkCompanySession(HttpSession session) {
        // TODO Auto-generated method stub
        return null;
    }

    public ResponseEntity<MessageDTO> checkCustomerSession(HttpSession session) {
        // TODO Auto-generated method stub
        return null;
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

    ///HELPER METHODS END

}   
