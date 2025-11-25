package com.shubilet.security_service.services;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.requests.SessionInfoDTO;

public interface CompanySessionService {
    public ResponseEntity<SessionInfoDTO> login(String email, String password);

    public ResponseEntity<Boolean> logout(int id);

    public ResponseEntity<Boolean> check(int id, String token);

    public boolean hasEmail(String email);
}

