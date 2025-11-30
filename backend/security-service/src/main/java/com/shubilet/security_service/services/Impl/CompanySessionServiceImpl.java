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

/**

    Domain: Session

    Provides the business logic for managing company session lifecycles within the
    application. This service acts as the central coordinator for authentication,
    session creation, validation, and termination operations, ensuring secure and
    consistent handling of session states. It interacts directly with the underlying
    repository layer to perform credential checks, maintain session persistence, and
    manage expiration rules. As part of the service layer in a typical Spring Boot
    architecture, it encapsulates domain-level operations and enforces session-related
    business constraints.

    <p>

        Technologies:

        <ul>
            <li>Spring Service</li>
            <li>Spring Data JPA</li>
        </ul>

    </p>

    @see CompanySessionRepository

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
@Service
public class CompanySessionServiceImpl implements CompanySessionService {
    private final CompanySessionRepository companySessionRepository;

    public CompanySessionServiceImpl(CompanySessionRepository companySessionRepository) {
        this.companySessionRepository = companySessionRepository;
    }

    /**

        Operation: Login

        Authenticates a company user by validating the supplied email and password against stored
        credentials. Upon successful authentication, the method generates a unique session key,
        ensures that it does not collide with an existing session code, and persists a new
        {@code CompanySession} entity with the correct expiration timestamp. The created session
        data is then returned as a {@code CookieDTO} containing the company identifier, user type,
        and generated session code. If credential validation fails, the method responds with an
        HTTP 401 Unauthorized status and no body.

        <p>

            Uses:

            <ul>
                <li>{@code companySessionRepository} for credential validation, company ID lookup,
                session-code uniqueness checks, and session persistence</li>

                <li>{@code SessionKeyGenerator} for generating unique session keys</li>
                <li>{@code AppConstants.DEFAULT_SESSION_EXPIRATION_DURATION} for defining session lifetime</li>
                <li>{@code CookieDTO} and {@code UserType.COMPANY} for representing session output</li>
            </ul>
        </p>

        @param email the company user's login email

        @param password the company user's login password

        @return a response entity containing a {@code CookieDTO} upon successful login, or a
        401 Unauthorized response when credentials are invalid


    */
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

    /**

        Operation: Logout

        Performs a logout operation by validating whether the session entry
        exists for the given identifier and removing it from the underlying
        persistence store. Returns a boolean indicator showing whether the
        logout process was successfully completed.

        <p>

            Uses:

            <ul>
                <li>CompanySessionRepository for session existence check and deletion</li>
            </ul>

        </p>

        @param id the unique identifier of the session to be terminated

        @return a response entity containing a boolean indicating logout success
    */
    public ResponseEntity<Boolean> logout(int id) {
        if (!companySessionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(false);
        }
        
        companySessionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    /**

        Operation: Validate

        Validates the company session by checking the existence of a matching
        session record, evaluating expiration status, and confirming whether
        the associated company has completed verification. Returns a structured
        status response reflecting the current session state and any validation
        failures.

        <p>

            Uses:

            <ul>
                <li>CompanySessionRepository for existence check, expiration evaluation, and company verification lookup</li>
            </ul>

        </p>

        @param companyId the identifier of the company whose session is being validated

        @param code the session code used to locate and verify the session record

        @return a response entity containing the session status result
    */
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

    public void cleanAllSessions() {
        companySessionRepository.deleteAll();
    }

    public void cleanExpiredSessions() {
        companySessionRepository.deleteExpiredSessions();
    }
}
