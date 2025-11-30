package com.shubilet.security_service.services;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.requests.CookieDTO;
import com.shubilet.security_service.dataTransferObjects.requests.StatusDTO;

/**

    Domain: Session

    Defines the service-level contract responsible for managing customer session
    lifecycles within the application. This interface groups all core operations
    related to authentication, session creation, validation, lookup, and maintenance.
    Implementations are expected to enforce security rules such as credential
    verification, token uniqueness, session expiration handling, and cleanup logic,
    while delegating persistence operations to the underlying repository layer.

    <p>

        Technologies:

        <ul>
            <li>Spring Web</li>
        </ul>
    </p>

    @see CustomerSessionRepository

    @version 1.0
*/
public interface CustomerSessionService {
    /**

        Operation: Login

        Authenticates a customer by validating the provided email and password against
        stored credentials. Upon successful verification, this operation initiates the
        session creation workflow, which typically includes generating a unique session
        code and persisting related session data. The resulting session details are then
        returned as a {@code CookieDTO}. If authentication fails, the implementation is
        expected to return an appropriate unauthorized response.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for credential validation and session persistence</li>
                <li>SessionKeyGenerator for generating unique session codes</li>
            </ul>

        </p>

        @param email the customer's login email

        @param password the customer's login password

        @return a response entity containing a {@code CookieDTO} representing the created session
    */
    public ResponseEntity<CookieDTO> login(String email, String password);

    /**

        Operation: Logout

        Terminates an active customer session by validating the existence of the session
        associated with the given identifier and removing it from the underlying storage.
        The returned boolean value indicates whether the logout process was completed
        successfully.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for session existence verification and deletion</li>
            </ul>

        </p>

        @param id the identifier of the session to be terminated

        @return a response entity containing a boolean representing logout success
    */
    public ResponseEntity<Boolean> logout(int id);

    /**

        Operation: Validate

        Validates a customer session by verifying that a session associated with the given
        identifier and token exists and is still active. The result is returned as a
        structured {@code StatusDTO} indicating whether the session is valid or the reason
        for failure, such as missing or expired session data.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for session existence checking and expiration evaluation</li>
            </ul>

        </p>

        @param id the identifier of the customer whose session is being validated
        @param token the session token used to verify the session record

        @return a response entity containing the session validation status
    */
    public ResponseEntity<StatusDTO> check(int id, String token);

    /**

        Operation: Lookup

        Checks whether the specified email exists in the underlying customer data store.
        This operation is typically used during validation processes such as registration
        checks, login preparation, or account recovery flows.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for email existence lookup</li>
            </ul>

        </p>

        @param email the email address to be checked

        @return {@code true} if the email exists, otherwise {@code false}
    */
    public boolean hasEmail(String email);
    
    /**

        Operation: Clean All Sessions

        Purges all existing customer session records from the underlying data store.
        This operation is typically used for maintenance tasks, testing scenarios,
        or administrative actions that require a complete reset of session data.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for bulk deletion of session records</li>
            </ul>

        </p>
    */
    public void cleanAllSessions();

    /**

        Operation: Clean Expired Sessions

        Removes all customer session records that have surpassed their expiration timestamps
        from the underlying data store. This operation helps maintain data integrity and
        optimizes storage by eliminating stale session entries.

        <p>

            Uses:

            <ul>
                <li>CustomerSessionRepository for identifying and deleting expired session records</li>
            </ul>

        </p>
    */
    public void cleanExpiredSessions();
}
