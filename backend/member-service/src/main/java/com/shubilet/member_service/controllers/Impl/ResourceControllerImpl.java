package com.shubilet.member_service.controllers.Impl;


import com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs.CompanyIdDTO;
import com.shubilet.member_service.dataTransferObjects.responses.CompanyIdNameMapDTO;
import com.shubilet.member_service.services.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/get")
public class ResourceControllerImpl {
    private static final Logger logger = LoggerFactory.getLogger(ResourceControllerImpl.class);
    private final ResourceService resourceService;

    public ResourceControllerImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("company/names")
    public ResponseEntity<CompanyIdNameMapDTO> sendCompanyNames(@RequestBody List<CompanyIdDTO> companyIDsDTO) {
        // DTO Existence Check
        if (companyIDsDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CompanyIdNameMapDTO("DTO Can't be Null"));
        }

        // Attributes Null or Blank Check
        if (companyIDsDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CompanyIdNameMapDTO("Company IDs Can't be Empty"));
        }

        HashMap<Integer, String> companyNamesMap = resourceService.sendCompanyNames(companyIDsDTO);
        if (companyNamesMap == null || companyNamesMap.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CompanyIdNameMapDTO("No Matching Company has been Found"));
        }

        return ResponseEntity.ok(new CompanyIdNameMapDTO(companyNamesMap, "Successfully Retrieved Company Names"));
    }
}
