package com.shubilet.member_service.services;

import com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs.CompanyIdDTO;

import java.util.HashMap;
import java.util.List;

public interface ResourceService {
    public HashMap<Integer, String> sendCompanyNames(List<CompanyIdDTO> companyIDsList);
}
