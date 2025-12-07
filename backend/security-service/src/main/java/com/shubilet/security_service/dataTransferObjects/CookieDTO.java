package com.shubilet.security_service.dataTransferObjects;

import com.shubilet.security_service.common.constants.SessionKeys;

public class CookieDTO {
    private String userId;
    private String userType;
    private String authCode;

    public CookieDTO() {

    }

    public CookieDTO(String userId, String userType, String authCode) {
        this.userId = userId;
        this.userType = userType;
        this.authCode = authCode;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
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

    public String getAttribute(String key) {
        switch (key) {
            case SessionKeys.USER_ID:
                return getUserId();
            case SessionKeys.USER_TYPE:
                return getUserType();
            case SessionKeys.AUTH_CODE:
                return getAuthCode();
            default:
                return null;
        }
    }

    public void setAttribute(String key, String value) {
        switch (key) {
            case SessionKeys.USER_ID:
                setUserId(value);
                break;
            case SessionKeys.USER_TYPE:
                setUserType(value);
                break;
            case SessionKeys.AUTH_CODE:
                setAuthCode(value);
                break;
            default:
                // Do nothing for unknown keys
                break;
        }
    }

    public void removeAttribute(String key) {
        switch (key) {
            case SessionKeys.USER_ID:
                setUserId(null);
                break;
            case SessionKeys.USER_TYPE:
                setUserType(null);
                break;
            case SessionKeys.AUTH_CODE:
                setAuthCode(null);
                break;
            default:
                // Do nothing for unknown keys
                break;
        }
    }
}
