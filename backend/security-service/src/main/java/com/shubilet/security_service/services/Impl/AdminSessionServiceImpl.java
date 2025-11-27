package com.shubilet.security_service.services.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shubilet.security_service.dataTransferObjects.requests.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;
import com.shubilet.security_service.models.AdminSession;
import com.shubilet.security_service.repositories.AdminSessionRepository;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.common.constants.AppConstants;
import com.shubilet.security_service.common.enums.SessionStatus;
import com.shubilet.security_service.common.enums.UserType;
import com.shubilet.security_service.common.util.SessionKeyGenerator;

@Service
public class AdminSessionServiceImpl implements AdminSessionService {
    private final AdminSessionRepository adminSessionRepository;

    public AdminSessionServiceImpl(AdminSessionRepository adminSessionRepository) {
        this.adminSessionRepository = adminSessionRepository;
    }

    ///TODO: Yorum satırları eklenecek
    
    public ResponseEntity<CookieDTO> login(String email, String password) {

        if(!adminSessionRepository.isEmailAndPasswordValid(email, password)) {
            return ResponseEntity.status(401).build();
        }

        int adminId = adminSessionRepository.getAdminIdByEmail(email);
        String code = "";

        while (true) {
            code = SessionKeyGenerator.generate();
            if (!adminSessionRepository.hasCode(code)) {
                break;
            }
        }

        AdminSession adminSession = new AdminSession(adminId, code, AppConstants.DEFAULT_SESSION_EXPIRATION_DURATION);

        adminSessionRepository.save(adminSession);

        return ResponseEntity.ok(new CookieDTO(adminId, UserType.ADMIN, code));
    }

    public ResponseEntity<Boolean> logout(int id) {
        if (!adminSessionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(false);
        }

        adminSessionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<StatusDTO> check(int adminId, String code) {

        if(!adminSessionRepository.existsByAdminIdAndCode(adminId, code)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.NOT_FOUND));
        }

        if(adminSessionRepository.isExpired(adminId, code)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.EXPIRED));
        }

        if(!adminSessionRepository.isVerifiedAdmin(adminId)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.NOT_VERIFIED));
        }

        return ResponseEntity.ok(new StatusDTO(SessionStatus.VALID));
    }

    public boolean hasEmail(String email) {
        return adminSessionRepository.hasEmail(email);
    }

    public boolean isVerifiedEmail(String email) {
        return adminSessionRepository.isVerifiedEmail(email);
    }
}