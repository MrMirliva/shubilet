package com.shubilet.expedition_service.services;

import java.util.List;

import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCustomerDTO;

public interface ExpeditionService {
    
    public int createExpedition(String departureCity, String arrivalCity, String date, String time, int capacity);

    public List<ExpeditionForCustomerDTO> findExpeditionsByInstantAndRoute(String departureCity, String arrivalCity, String date);

    public List<ExpeditionForCompanyDTO> findExpeditionsByInstant(String date);

    public List<ExpeditionForCompanyDTO> findUpcomingExpeditions(int companyId);

    public List<ExpeditionForCompanyDTO> findAllExpeditions(int companyId);

    public boolean doesExpeditionExist(int expeditionId);
}
