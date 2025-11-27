package com.shubilet.security_service.services.Impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.shubilet.security_service.common.constants.AppConstants;
import com.shubilet.security_service.common.enums.SessionStatus;
import com.shubilet.security_service.common.enums.UserType;
import com.shubilet.security_service.common.util.SessionKeyGenerator;
import com.shubilet.security_service.dataTransferObjects.requests.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;
import com.shubilet.security_service.models.CustomerSession;
import com.shubilet.security_service.repositories.CustomerSessionRepository;
import com.shubilet.security_service.services.CustomerSessionService;

@Service
public class CustomerSessionServiceImpl implements CustomerSessionService {
    private final CustomerSessionRepository customerSessionRepository;

    public CustomerSessionServiceImpl(CustomerSessionRepository customerSessionRepository) {
        this.customerSessionRepository = customerSessionRepository;
    }

    ///TODO: Yorum satırları eklenecek
    
    public ResponseEntity<CookieDTO> login(String email, String password) {
        if(!customerSessionRepository.isEmailAndPasswordValid(email, password)) {
            return ResponseEntity.status(401).build();
        }

        int customerId = customerSessionRepository.getCustomerIdByEmail(email);
        String code = "";

        while (true) {
            code = SessionKeyGenerator.generate();
            if (!customerSessionRepository.hasCode(code)) {
                break;
            }
        }

        CustomerSession customerSession = new CustomerSession(customerId, code, AppConstants.DEFAULT_SESSION_EXPIRATION_DURATION);

        customerSessionRepository.save(customerSession);

        return ResponseEntity.ok(new CookieDTO(customerId, UserType.CUSTOMER, code));
    }

    public ResponseEntity<Boolean> logout(int id) {
        if (!customerSessionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(false);
        }
        
        customerSessionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    public ResponseEntity<StatusDTO> check(int adminId, String code) {

        if(!customerSessionRepository.existsByCustomerIdAndCode(adminId, code)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.NOT_FOUND));
        }

        if(customerSessionRepository.isExpired(adminId, code)) {
            return ResponseEntity.badRequest().body(new StatusDTO(SessionStatus.EXPIRED));
        }

        return ResponseEntity.ok(new StatusDTO(SessionStatus.VALID));
    }

    public boolean hasEmail(String email) {
        return customerSessionRepository.hasEmail(email);
    }
}
