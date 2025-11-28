package com.shubilet.security_service.services;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.requests.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;

public interface CompanySessionService {
    public ResponseEntity<CookieDTO> login(String email, String password);

    public ResponseEntity<Boolean> logout(int id);

    public ResponseEntity<StatusDTO> check(int id, String token);

    public boolean hasEmail(String email);

    public boolean isVerifiedEmail(String email);

    public void cleanAllSessions();

}

