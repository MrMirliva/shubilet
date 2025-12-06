package com.shubilet.security_service.controllers;

import org.springframework.http.ResponseEntity;

import com.shubilet.security_service.dataTransferObjects.requests.LoginDTO;
import com.shubilet.security_service.dataTransferObjects.responses.CheckMessageDTO;
import com.shubilet.security_service.dataTransferObjects.responses.MessageDTO;

import jakarta.servlet.http.HttpSession;



public interface AuthController {

    
    public ResponseEntity<MessageDTO> createSession (LoginDTO loginDTO, HttpSession session);

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

    
    public ResponseEntity<CheckMessageDTO> checkAdminSession (HttpSession session);
    
    public ResponseEntity<CheckMessageDTO> checkCompanySession (HttpSession session);

    public ResponseEntity<CheckMessageDTO> checkCustomerSession (HttpSession session);
}
