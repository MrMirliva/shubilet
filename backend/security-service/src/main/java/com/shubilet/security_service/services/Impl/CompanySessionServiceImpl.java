package com.shubilet.security_service.services.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shubilet.security_service.common.constants.AppConstants;
import com.shubilet.security_service.common.enums.SessionStatus;
import com.shubilet.security_service.common.enums.UserType;
import com.shubilet.security_service.common.util.SessionKeyGenerator;
import com.shubilet.security_service.dataTransferObjects.requests.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;
import com.shubilet.security_service.models.CompanySession;
import com.shubilet.security_service.repositories.CompanySessionRepository;
import com.shubilet.security_service.services.CompanySessionService;

@Service
public class CompanySessionServiceImpl implements CompanySessionService {
    private final CompanySessionRepository companySessionRepository;

    public CompanySessionServiceImpl(CompanySessionRepository companySessionRepository) {
        this.companySessionRepository = companySessionRepository;
    }

    ///TODO: Yorum satırları eklenecek

    public ResponseEntity<CookieDTO> login(String email, String password) {
        if(!companySessionRepository.isEmailAndPasswordValid(email, password)) {
            return ResponseEntity.status(401).build();
        }

        int companyId = companySessionRepository.getCompanyIdByEmail(email);
        String code = "";

        while (true) {
            code = SessionKeyGenerator.generate();
            if (!companySessionRepository.hasCode(code)) {
                break;
            }
        }

        CompanySession companySession = new CompanySession(companyId, code, AppConstants.DEFAULT_SESSION_EXPIRATION_DURATION);

        companySessionRepository.save(companySession);

        return ResponseEntity.ok(new CookieDTO(companyId, UserType.COMPANY, code));
    }

    public ResponseEntity<Boolean> logout(int id) {
        if (!companySessionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(false);
        }
        
        companySessionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<StatusDTO> check(int companyId, String code) {

        if(!companySessionRepository.existsByCompanyIdAndCode(companyId, code)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.NOT_FOUND));
        }

        if(companySessionRepository.isExpired(companyId, code)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.EXPIRED));
        }

        if(!companySessionRepository.isVerifiedCompany(companyId)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.NOT_VERIFIED));
        }

        return ResponseEntity.ok(new StatusDTO(SessionStatus.VALID));
    }

    public boolean hasEmail(String email) {
        return companySessionRepository.hasEmail(email);
    }

    public boolean isVerifiedEmail(String email) {
        return companySessionRepository.isVerifiedEmail(email);
    }
}
