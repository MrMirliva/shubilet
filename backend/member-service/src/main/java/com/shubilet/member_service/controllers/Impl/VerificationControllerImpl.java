package com.shubilet.member_service.controllers.Impl;

import com.shubilet.member_service.controllers.VerificationController;
import com.shubilet.member_service.dataTransferObjects.requests.AdminVerificationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CompanyVerificationDTO;
import com.shubilet.member_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.member_service.services.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verify")
public class VerificationControllerImpl implements VerificationController {
    private final Logger logger = LoggerFactory.getLogger(VerificationControllerImpl.class);
    private final VerificationService verificationService;

    public VerificationControllerImpl(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/company")
    public ResponseEntity<MessageDTO> verifyCompany(@RequestBody CompanyVerificationDTO companyVerificationDTO) {
        // DTO Existence Check
        if (companyVerificationDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("DTO is null"));
        }
        logger.info("Company Verification Request Received. IDs: {} {}", companyVerificationDTO.getAdminId(), companyVerificationDTO.getCandidateCompanyId());

        // Validation Check
        if (companyVerificationDTO.getAdminId() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Admin ID"));
        }

        if (companyVerificationDTO.getCandidateCompanyId() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Candidate Company ID"));
        }

        if (!verificationService.isAdminExists(companyVerificationDTO.getAdminId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Admin with Given ID does not Exist"));
        }

        if (!verificationService.isCompanyExists(companyVerificationDTO.getCandidateCompanyId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Candidate Company with Given ID does not Exist"));
        }

        if (!verificationService.hasClearance(companyVerificationDTO.getAdminId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageDTO("Admin does not have the Necessary Clearance"));
        }
        if (!verificationService.markCompanyVerified(companyVerificationDTO.getAdminId(), companyVerificationDTO.getCandidateCompanyId())){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Candidate Company Marked as Verified Successfully"));
    }

    @PostMapping("/admin")
    public ResponseEntity<MessageDTO> verifyAdmin(@RequestBody AdminVerificationDTO adminVerificationDTO)   {
        // DTO Existence Check
        if (adminVerificationDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("DTO is null"));
        }

        logger.info("Admin Verification Request Received. IDs: {} {}", adminVerificationDTO.getAdminId(), adminVerificationDTO.getCandidateAdminId());
        // Validation Check
        if (adminVerificationDTO.getAdminId() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Admin ID"));
        }

        if (adminVerificationDTO.getCandidateAdminId() < 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Invalid Candidate Admin ID"));
        }

        if (!verificationService.isAdminExists(adminVerificationDTO.getAdminId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Admin with Given ID does not Exist"));
        }

        if (!verificationService.isAdminExists(adminVerificationDTO.getCandidateAdminId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageDTO("Candidate Admin with Given ID does not Exist"));
        }

        if (!verificationService.hasClearance(adminVerificationDTO.getAdminId())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageDTO("Admin does not have the Necessary Clearance"));
        }
        if (!verificationService.markAdminVerified(adminVerificationDTO.getAdminId(), adminVerificationDTO.getCandidateAdminId())){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new MessageDTO("Critical Error"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageDTO("Candidate Admin Marked as Verified Successfully"));
    }


}
