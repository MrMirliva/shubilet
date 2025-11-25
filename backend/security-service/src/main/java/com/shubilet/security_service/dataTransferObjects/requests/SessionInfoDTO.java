package com.shubilet.security_service.dataTransferObjects.requests;

public class SessionInfoDTO {
    private int userId;
    private String userType;
    private String authCode;

    public SessionInfoDTO(int userId, String userType, String authCode) {
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
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}