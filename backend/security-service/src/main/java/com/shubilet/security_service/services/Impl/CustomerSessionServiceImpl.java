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

/**

    Domain: Session
    
    Provides the core business logic for managing customer session lifecycles within
    the application. This service is responsible for authenticating customers,
    creating and persisting session entries, validating active sessions, and
    terminating sessions when requested or required by business rules. It acts as
    a service-layer component in a Spring Boot architecture, delegating persistence
    operations to the repository layer while enforcing session-related constraints
    such as expiration handling and credential validation.

    <p>

        Technologies:

        <ul>
            <li>Spring Service</li>
            <li>Spring Data JPA</li>
        </ul>

    </p>

    @see CustomerSessionRepository

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
@Service
public class CustomerSessionServiceImpl implements CustomerSessionService {
    private final CustomerSessionRepository customerSessionRepository;

    public CustomerSessionServiceImpl(CustomerSessionRepository customerSessionRepository) {
        this.customerSessionRepository = customerSessionRepository;
    }

    
    /**

        Operation: Login

        Authenticates a customer by validating the provided email and password against
        stored credentials. Upon successful authentication, the method generates a
        unique session code, ensures its uniqueness by repeatedly checking for conflicts,
        and persists a new {@code CustomerSession} with an appropriate expiration timestamp.
        The generated session information is returned as a {@code CookieDTO} containing
        the customer identifier, user type, and session code. If authentication fails,
        the method responds with an HTTP 401 Unauthorized status.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for credential validation, customer ID retrieval,
                session-code uniqueness checks, and session persistence</li>

                <li>SessionKeyGenerator for generating unique session codes</li>
                <li>AppConstants.DEFAULT_SESSION_EXPIRATION_DURATION for determining session lifetime</li>
            </ul>

        </p>

        @param email the customer's login email

        @param password the customer's login password

        @return a response entity containing a {@code CookieDTO} upon success, or an
        HTTP 401 response when authentication fails


    */
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

    /**

        Operation: Logout

        Performs a logout operation by verifying the existence of a session associated
        with the given identifier and removing it from the persistence layer. Returns
        a boolean value indicating whether the logout was successfully completed,
        responding with HTTP 404 when the session does not exist.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for session existence verification and deletion</li>
            </ul>

        </p>

        @param id the unique identifier of the session to be terminated

        @return a response entity containing a boolean result indicating logout success
    */
    public ResponseEntity<Boolean> logout(int id) {
        if (!customerSessionRepository.existsById(id)) {
            return ResponseEntity.status(404).body(false);
        }
        
        customerSessionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    /**

        Operation: Validate

        Checks the validity of a customer session by confirming the existence of a matching
        session record and verifying whether the session has expired. Returns an appropriate
        status response indicating whether the session is valid or the specific reason for
        failure.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for existence validation and expiration checks</li>
            </ul>

        </p>

        @param adminId the identifier of the customer whose session is being validated

        @param code the session code used to verify the session record

        @return a response entity containing the session validation status
    */
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

    public void cleanAllSessions() {
        customerSessionRepository.deleteAll();
    }

    public void cleanExpiredSessions() {
        customerSessionRepository.deleteExpiredSessions();
    }
}
