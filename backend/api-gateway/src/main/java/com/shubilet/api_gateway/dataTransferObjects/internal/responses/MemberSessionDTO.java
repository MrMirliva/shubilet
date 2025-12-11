package com.shubilet.api_gateway.dataTransferObjects.internal.responses;


public class MemberSessionDTO {
    private int userId;
    private String userType;

    MemberSessionDTO() {

    }
    MemberSessionDTO(int userId, String userType) {
        this.userId = userId;
        this.userType = userType;
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
}
