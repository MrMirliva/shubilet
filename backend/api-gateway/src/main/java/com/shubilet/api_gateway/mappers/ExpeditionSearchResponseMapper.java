package com.shubilet.api_gateway.mappers;

import com.shubilet.api_gateway.dataTransferObjects.internal.ExpeditionForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.CompanyIdDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.requests.CompanyIdsDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.ExpeditionsForCustomerDTO;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ExpeditionSearchResponseMapper {

    @Mapping(source = "e.companyId", target = "companyId")
    public CompanyIdDTO toCompanyIdDTO(ExpeditionForCustomerDTO e);

    public List<CompanyIdDTO> toCompanyIdDTOs(List<ExpeditionForCustomerDTO> expeditionForCustomerDTOs);
}
