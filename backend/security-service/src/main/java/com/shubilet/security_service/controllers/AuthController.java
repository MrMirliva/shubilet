package com.shubilet.security_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.LoginDTO;
import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;



public interface AuthController {

    public ResponseEntity<MessageDTO> createSession (LoginDTO loginDTO);

    public ResponseEntity<MessageDTO> logout (CookieDTO session);

    public ResponseEntity<MessageDTO> check (CookieDTO session);

    public ResponseEntity<CheckMessageDTO> checkAdminSession (CookieDTO session);
    
    public ResponseEntity<CheckMessageDTO> checkCompanySession (CookieDTO session);

    public ResponseEntity<CheckMessageDTO> checkCustomerSession (CookieDTO session);
}
