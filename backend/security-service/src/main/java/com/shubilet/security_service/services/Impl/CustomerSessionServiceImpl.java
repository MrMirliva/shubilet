package com.shubilet.security_service.services.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shubilet.security_service.dataTransferObjects.requests.SessionInfoDTO;
import com.shubilet.security_service.repositories.CustomerSessionRepository;
import com.shubilet.security_service.services.CustomerSessionService;

@Service
public class CustomerSessionServiceImpl implements CustomerSessionService {
    private final CustomerSessionRepository customerSessionRepository;

    public CustomerSessionServiceImpl(CustomerSessionRepository customerSessionRepository) {
        this.customerSessionRepository = customerSessionRepository;
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
}
