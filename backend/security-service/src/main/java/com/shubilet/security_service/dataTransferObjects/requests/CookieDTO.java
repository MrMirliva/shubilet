package com.shubilet.security_service.dataTransferObjects.requests;

import com.shubilet.security_service.common.enums.UserType;

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