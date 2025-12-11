package com.shubilet.member_service.services.Impl;

import com.shubilet.member_service.models.Admin;
import com.shubilet.member_service.repositories.AdminRepository;
import com.shubilet.member_service.repositories.CompanyRepository;
import com.shubilet.member_service.services.VerificationService;
import org.springframework.stereotype.Service;


@Service
public class VerificationServiceImpl implements VerificationService {
    private CompanyRepository companyRepository;
    private AdminRepository adminRepository;

    public VerificationServiceImpl(CompanyRepository companyRepository, AdminRepository adminRepository) {
        this.companyRepository = companyRepository;
        this.adminRepository = adminRepository;
    }

    public boolean isCompanyExists(int companyId){
        return companyRepository.existsById(companyId);
    }
    public boolean isAdminExists(int adminId) {
        return adminRepository.existsById(adminId);
    }


    public boolean hasClearance(int adminId) {
        Admin admin = adminRepository.getAdminById(adminId);

        // Admin is not Verified
        if (admin == null || admin.getRefAdminId() == null) {
            return false;
        }
        return true;

    }

    public boolean markCompanyVerified(int companyId) {
        return true;
    }

    public boolean markAdminVerified(int adminId) {
        return true;
    }
}
