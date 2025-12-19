package com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs;

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
