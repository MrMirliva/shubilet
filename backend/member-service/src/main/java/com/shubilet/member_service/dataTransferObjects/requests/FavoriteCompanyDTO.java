package com.shubilet.member_service.dataTransferObjects.requests;

public class FavoriteCompanyDTO {
    private int relationId;
    private int customerId;
    private int companyId;

    public FavoriteCompanyDTO(int relationId) {
        this.relationId = relationId;
    }

    public FavoriteCompanyDTO(int customerId, int companyId) {
        this.customerId = customerId;
        this.companyId = companyId;
    }

    public int getRelationId() {
        return relationId;
    }

    public void setRelationId(int relationId) {
        this.relationId = relationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
}
