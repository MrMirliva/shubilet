package com.shubilet.security_service.services.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shubilet.security_service.dataTransferObjects.requests.SessionInfoDTO;
import com.shubilet.security_service.models.AdminSession;
import com.shubilet.security_service.repositories.AdminSessionRepository;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.common.constants.AppConstants;
import com.shubilet.security_service.common.enums.UserType;
import com.shubilet.security_service.common.util.SessionKeyGenerator;

@Service
public class AdminSessionServiceImpl implements AdminSessionService {
    private final AdminSessionRepository adminSessionRepository;

    public AdminSessionServiceImpl(AdminSessionRepository adminSessionRepository) {
        this.adminSessionRepository = adminSessionRepository;
    }

    ///TODO: Yorum satırları eklenecek
    
    public ResponseEntity<SessionInfoDTO> login(String email, String password) {

        if(!adminSessionRepository.isEmailAndPasswordValid(email, password)) {
            return ResponseEntity.status(401).build();
        }

        int userId = adminSessionRepository.getUserIdByEmail(email);
        String code = "";

        while (true) {
            code = SessionKeyGenerator.generate();
            if (!adminSessionRepository.hasCode(code)) {
                break;
            }
        }

        AdminSession adminSession = new AdminSession(userId, code, AppConstants.DEFAULT_SESSION_EXPIRATION_DURATION);

        adminSessionRepository.save(adminSession);

        return ResponseEntity.ok(new SessionInfoDTO(userId, UserType.ADMIN, code));
    }

    public ResponseEntity<Boolean> logout(int id) {
        if (!adminSessionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(false);
        }

        adminSessionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<Boolean> check(int userId, String code) {
        return ResponseEntity.ok(adminSessionRepository.existsByUserIdAndCode(userId, code));
    }

    public boolean hasEmail(String email) {
        return adminSessionRepository.hasEmail(email);
    }

    public boolean isVerifiedEmail(String email) {
        return adminSessionRepository.isVerifiedEmail(email);
    }
}