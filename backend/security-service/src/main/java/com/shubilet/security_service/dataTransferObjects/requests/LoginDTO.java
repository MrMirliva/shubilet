package com.shubilet.security_service.dataTransferObjects.requests;

public class LoginDTO {
    private int userId;

    public LoginDTO() {
    }

    public LoginDTO(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}