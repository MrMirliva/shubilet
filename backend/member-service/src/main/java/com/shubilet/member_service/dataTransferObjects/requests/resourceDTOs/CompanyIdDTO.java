package com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs;

import java.util.LinkedList;

public class CompanyIdDTO {
    private Integer companyId;

    public CompanyIdDTO() {

    }

    public CompanyIdDTO(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
}
