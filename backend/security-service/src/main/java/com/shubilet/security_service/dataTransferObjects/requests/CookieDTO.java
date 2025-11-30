package com.shubilet.security_service.dataTransferObjects.requests;

import com.shubilet.security_service.common.enums.UserType;

/**

    Domain: Session

    Represents the data transfer object used to transport authenticated session information
    between backend session services and the authentication controller. This DTO encapsulates
    the user identifier, user type, and authorization code required to establish or validate
    a user session. It serves as a simple container for session-related values that can be
    serialized, deserialized, and stored in the HTTP session as needed.

    <p>

        Technologies:

        <ul>
            <li>Core Java DTO pattern</li>
            <li>{@code UserType} enum for role representation</li>
        </ul>

    </p>

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
public class CookieDTO {
    private int userId;
    private UserType userType;
    private String authCode;

    public CookieDTO(int userId, UserType userType, String authCode) {
        this.userId = userId;
        this.userType = userType;
        this.authCode = authCode;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserType() {
        return userType.getCode();
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}