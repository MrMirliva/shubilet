package com.shubilet.api_gateway.mappers;

import com.shubilet.api_gateway.dataTransferObjects.external.responses.expeditionOperations.ExpeditionSearchResultCompanyDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.ExpeditionForCustomerDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.CompanyIdNameMapDTO;
import com.shubilet.api_gateway.dataTransferObjects.internal.responses.expeditionOperations.ExpeditionsForCustomerDTO;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CompanyIdNameMapper {

    public CompanyIdNameMapper() {
        throw new UnsupportedOperationException("Mapper class cannot be instantiated.");
    }

    public static List<ExpeditionSearchResultCompanyDTO> toExpeditionSearchResultsDTO(ExpeditionsForCustomerDTO expeditionsForCustomerDTO, CompanyIdNameMapDTO companyIdNameMapDTO) {
        List<ExpeditionSearchResultCompanyDTO> matchedExpeditions = new LinkedList<>();
        HashMap<Integer, String> companyMap = companyIdNameMapDTO.getCompanies();

        for (ExpeditionForCustomerDTO expedition : expeditionsForCustomerDTO.getExpeditions()) {
            matchedExpeditions.add(new ExpeditionSearchResultCompanyDTO(
                            expedition.getExpeditionId(),
                            expedition.getDepartureCity(),
                            expedition.getArrivalCity(),
                            expedition.getDate(),
                            expedition.getTime(),
                            expedition.getPrice(),
                            expedition.getDuration(),
                            companyMap.get(expedition.getCompanyId())
                    )
            );
        }
        return matchedExpeditions;
    }
}
