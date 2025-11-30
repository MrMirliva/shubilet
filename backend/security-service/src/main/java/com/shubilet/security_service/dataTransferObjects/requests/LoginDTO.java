package com.shubilet.security_service.dataTransferObjects.requests;

/**

    Domain: Authentication

    Serves as a data transfer object encapsulating the user’s login credentials submitted
    during the authentication process. This DTO carries the email and password fields used
    by the authentication controller and validation utilities to authenticate a user against
    backend session or identity services. It represents the minimal, structured input required
    to initiate a login request.

    <p>

        Technologies:

        <ul>
            <li>Core Java DTO pattern</li>
        </ul>
        
    </p>

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
public class LoginDTO {
    private String email;
    private String password;

    public LoginDTO() {
    }

    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}