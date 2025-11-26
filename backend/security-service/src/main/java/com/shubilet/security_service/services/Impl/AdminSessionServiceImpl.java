package com.shubilet.security_service.services.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shubilet.security_service.dataTransferObjects.requests.SessionInfoDTO;
import com.shubilet.security_service.repositories.AdminSessionRepository;
import com.shubilet.security_service.services.AdminSessionService;

@Service
public class AdminSessionServiceImpl implements AdminSessionService {
    private final AdminSessionRepository adminSessionRepository;

    public AdminSessionServiceImpl(AdminSessionRepository adminSessionRepository) {
        this.adminSessionRepository = adminSessionRepository;
    }

    public ResponseEntity<SessionInfoDTO> login(String email, String password) {
        // Implementation here
        return null;
    }

    public ResponseEntity<Boolean> logout(int id) {
        // Implementation here
        return null;
    }

    public ResponseEntity<Boolean> check(int id, String token) {
        // Implementation here
        return null;
    }

    public boolean hasEmail(String email) {
        // Implementation here
        return false;
    }

    public boolean isVerifiedEmail(String email) {
        // Implementation here
        return false;
    }
}