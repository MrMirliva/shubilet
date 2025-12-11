package com.shubilet.member_service.services;

public interface VerificationService {
    public boolean hasClearance(int adminId);

    public boolean markCompanyVerified(int companyId);

    public boolean markAdminVerified(int adminId);

    public boolean isAdminExists(int adminId);

    public boolean isCompanyExists(int adminId);

}

