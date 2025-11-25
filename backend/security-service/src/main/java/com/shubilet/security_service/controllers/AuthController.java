package com.shubilet.security_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

import jakarta.servlet.http.HttpSession;

public interface AuthController {
    public ResponseEntity<MessageDTO> login (String email, String password, HttpSession session);

    public ResponseEntity<MessageDTO> logout (HttpSession session);

    public ResponseEntity<MessageDTO> check (HttpSession session);

    public ResponseEntity<MessageDTO> checkAdminSession (HttpSession session);
    
    public ResponseEntity<MessageDTO> checkCompanySession (HttpSession session);

    public ResponseEntity<MessageDTO> checkCustomerSession (HttpSession session);
}
