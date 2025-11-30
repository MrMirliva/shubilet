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

/**

    Domain: Session

    Provides the concrete service-layer implementation for managing administrator session
    operations within the authentication subsystem. This class coordinates credential
    validation, session creation, session termination, and session-status verification by
    delegating persistence and lookup responsibilities to the underlying repository. It also
    offers helper utilities for checking email existence, email verification status, and
    performing session cleanup tasks. The service centralizes all admin-session lifecycle
    operations, ensuring consistency and encapsulation of session-related business logic.

    <p>

        Technologies:

        <ul>
            <li>Spring Service Layer</li>
            <li>Spring Web {@code ResponseEntity} for API-friendly responses</li>
            <li>JPA Repository abstraction for persistence operations</li>
            <li>{@code SessionKeyGenerator} for secure session-code generation</li>
            <li>{@code AppConstants} for session expiration configuration</li>
        </ul>

    </p>

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMirliva

    @version 1.0
*/
@Service
public class AdminSessionServiceImpl implements AdminSessionService {
    private final AdminSessionRepository adminSessionRepository;

    public AdminSessionServiceImpl(AdminSessionRepository adminSessionRepository) {
        this.adminSessionRepository = adminSessionRepository;
    }
    
    /**

        Operation: Login

        Authenticates an administrator by validating the provided email and password against
        stored credentials. When authentication succeeds, a unique session key is generated,
        verified for uniqueness, and persisted as a new admin session with an assigned
        expiration timestamp. Returns a {@code CookieDTO} containing the admin’s identifier,
        user type, and generated session code. If credentials are invalid, the method responds
        with an HTTP 401 status without a body.

        <p>

            Uses:

            <ul>
                <li>{@code adminSessionRepository} for credential validation, ID lookup, code uniqueness checks, and session persistence</li>
                <li>{@code SessionKeyGenerator} for generating unique authenticated session codes</li>
                <li>{@code AppConstants.DEFAULT_SESSION_EXPIRATION_DURATION} for assigning expiration timestamps</li>
                <li>{@code CookieDTO} and {@code UserType.ADMIN} for structured session output</li>
            </ul>

        </p>

        @param email the administrator’s email used for authentication

        @param password the administrator’s password used for credential verification

        @return a response entity containing a populated {@code CookieDTO} on successful login,
        or a 401 Unauthorized response when credentials do not match a valid administrator
    */
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

    /**

        Operation: Logout

        Handles the administrator logout process by validating the existence of the session record
        associated with the provided identifier. If a matching session exists, it is removed from
        persistence, effectively terminating the admin’s authenticated state. When no such session
        exists, the method returns a 404 response indicating that the session could not be found.

        <p>

            Uses:

            <ul>
                <li>{@code adminSessionRepository} for existence checks and deletion of session records</li>
            </ul>
        </p>

        @param id the identifier of the admin session to be terminated

        @return a response entity containing {@code true} when logout succeeds, or a 404 response

        with {@code false} when the session does not exist
    */
    public ResponseEntity<Boolean> logout(int id) {
        if (!adminSessionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(false);
        }

        adminSessionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    /**

        Operation: Validate

        Validates the current administrator session by checking its existence, expiration state,
        and verification status. The method first ensures that a session matching the provided
        admin identifier and session code exists. It then evaluates whether the session has
        expired and finally confirms that the administrator account is verified. Each failure
        condition results in a structured {@code StatusDTO} describing the specific session
        status issue.

        <p>

            Uses:

            <ul>
                <li>{@code adminSessionRepository} for existence checks, expiration evaluation, and admin verification</li>
                <li>{@code StatusDTO} and {@code SessionStatus} for structured session-state reporting</li>
            </ul>

        </p>

        @param adminId the administrator’s identifier associated with the session

        @param code the session code that uniquely identifies the admin session

        @return a response entity containing a {@code StatusDTO} indicating whether the session

        is valid, expired, unverified, or not found
    */
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

    public void cleanAllSessions() {
        adminSessionRepository.deleteAll();
    }

    public void cleanExpiredSessions() {
        adminSessionRepository.deleteExpiredSessions();
    }
}