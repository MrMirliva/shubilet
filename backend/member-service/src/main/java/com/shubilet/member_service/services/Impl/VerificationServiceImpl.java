package com.shubilet.member_service.services.Impl;

import com.shubilet.member_service.models.Admin;
import com.shubilet.member_service.models.Company;
import com.shubilet.member_service.repositories.AdminRepository;
import com.shubilet.member_service.repositories.CompanyRepository;
import com.shubilet.member_service.services.VerificationService;
import org.springframework.stereotype.Service;

import java.util.List;


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

    public boolean markCompanyVerified(int adminId, int candidateCompanyId) {
        List<Company> company_result = companyRepository.getCompanyById(candidateCompanyId);
        if (company_result == null || company_result.isEmpty()) {
            return false;
        }
        Company company = company_result.getFirst();
        company.setVerified(true);
        company.setRefAdminId(adminId);
        companyRepository.save(company);
        return true;
    }

    public boolean markAdminVerified(int adminId, int candidateAdminId) {
        Admin admin = adminRepository.getAdminById(candidateAdminId);
        if (admin == null) {
            return false;
        }
        admin.setRefAdminId(adminId);
        adminRepository.save(admin);
        return true;
    }
}
