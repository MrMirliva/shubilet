package com.shubilet.security_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.requests.LoginDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

import jakarta.servlet.http.HttpSession;


/**

    Domain: Authentication

    Represents the contract for all authentication-related operations in the security service.
    This interface defines endpoint behaviors for login, logout, and session validation across
    multiple user roles (admin, company, customer). Implementations of this interface are
    responsible for enforcing authentication rules, handling session integrity, and returning
    standardized responses for both successful and erroneous authentication scenarios.
    It serves as the core abstraction separating REST API specifications from underlying
    authentication and session-management logic.

    <p>

        Technologies:

        <ul>
            <li>Spring Web (mapping interface methods to REST endpoints)</li>
            <li>Jakarta Servlet HttpSession for session-based authentication</li>
        </ul>

    </p>

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
public interface AuthController {

    /**

        Operation: Login

        Defines the contract for handling user login requests by accepting credential input
        and the current HTTP session. Implementations are expected to validate the provided
        credentials, authenticate the user against the appropriate backend system, and update
        the session with authenticated user context. Error responses should be produced for
        invalid input, incorrect credentials, unverified accounts, or conflicting session state.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for storing authenticated user information</li>
                <li>{@code LoginDTO} as the credential input payload</li>
            </ul>

        </p>

        @param loginDTO the credential payload containing email and password

        @param session the HTTP session to validate and populate upon successful authentication

        @return a response entity containing either a success message or a descriptive error message
    */
    public ResponseEntity<MessageDTO> login (LoginDTO loginDTO, HttpSession session);

    /**

        Operation: Logout

        Defines the contract for performing a user logout operation by validating the current
        HTTP session and clearing any authentication-related attributes. Implementations are
        expected to communicate with the appropriate backend session service to ensure that the
        user’s session is properly terminated, and to return error responses when the session is
        missing, invalid, or does not correspond to an active authenticated user.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for accessing and clearing session-scoped authentication data</li>
            </ul>

        </p>

        @param session the current HTTP session to be validated and cleared during logout

        @return a response entity containing a success message when logout completes successfully,
        or an error message when session validation or termination fails
    */
    public ResponseEntity<MessageDTO> logout (HttpSession session);

    /**

        Operation: Validate

        Defines the contract for validating the current HTTP session by checking whether the
        session contains the required authentication attributes and ensuring that they
        represent a valid, active user session. Implementations should verify structural
        integrity, format correctness, and session consistency before returning a success
        result or a descriptive error message.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for accessing stored authentication attributes</li>
            </ul>

        </p>

        @param session the HTTP session containing user authentication details to be validated

        @return a response entity containing a success message when the session is valid,
        or an appropriate error message when validation fails
    */
    public ResponseEntity<MessageDTO> check (HttpSession session);

    /**

        Operation: Validate

        Defines the contract for validating that the current HTTP session belongs to an
        authenticated administrator. Implementations should first verify generic session
        validity and then enforce that the session’s user type corresponds to an admin
        account. The result must indicate whether the admin session is valid or provide a
        structured error response when validation fails.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for accessing admin-related session attributes</li>
            </ul>

        </p>

        @param session the HTTP session expected to contain authenticated admin context

        @return a response entity containing a success message when the admin session is valid,
        or an error message when the session is missing, invalid, or not an admin session
    */
    public ResponseEntity<MessageDTO> checkAdminSession (HttpSession session);
    
    /**

        Operation: Validate

        Defines the contract for validating that the current HTTP session belongs to an
        authenticated company user. Implementations should first confirm general session
        integrity and then enforce that the session’s user type corresponds to a company
        account. A structured success response must be returned for valid sessions, while
        descriptive error messages should be produced when validation fails.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for reading company-related session attributes</li>
            </ul>

        </p>

        @param session the HTTP session expected to contain authenticated company user data

        @return a response entity containing a success message when the company session is valid,
        or an error message when the session is missing, invalid, or not a company session
    */
    public ResponseEntity<MessageDTO> checkCompanySession (HttpSession session);

    /**

        Operation: Validate

        Defines the contract for validating that the current HTTP session belongs to an
        authenticated customer user. Implementations should confirm the general validity
        of the session and ensure that the stored user type corresponds to a customer
        account. A successful validation must result in a positive response, while any
        inconsistency or mismatch should yield a structured error message.

        <p>

            Uses:

            <ul>
                <li>{@code HttpSession} for accessing customer-related session attributes</li>
            </ul>

        </p>

        @param session the HTTP session expected to contain authenticated customer context

        @return a response entity containing a success message when the customer session is valid,
        or an error message when the session is missing, invalid, or does not represent a customer
    */
    public ResponseEntity<MessageDTO> checkCustomerSession (HttpSession session);
}
