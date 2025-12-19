package com.shubilet.api_gateway.dataTransferObjects.internal.requests;

public class CompanyIdDTO {
    private Integer companyId;

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
