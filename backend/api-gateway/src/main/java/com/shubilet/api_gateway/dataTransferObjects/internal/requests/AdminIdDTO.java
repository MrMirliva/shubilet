package com.shubilet.api_gateway.dataTransferObjects.internal.requests;


public class AdminIdDTO {
    private Integer adminId;

    public AdminIdDTO() {

    }

    public AdminIdDTO(Integer adminId) {
        this.adminId = adminId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }
}
