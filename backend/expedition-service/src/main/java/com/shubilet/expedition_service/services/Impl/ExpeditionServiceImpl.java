package com.shubilet.expedition_service.services.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCustomerDTO;
import com.shubilet.expedition_service.services.ExpeditionService;

@Service
public class ExpeditionServiceImpl implements ExpeditionService {

    public int createExpedition(String departureCity, String arrivalCity, String date, String time, int capacity) {
        ///TODO: Create expedition logic to be implemented.
        return 0;
    }

    public List<ExpeditionForCustomerDTO> findExpeditionsByInstantAndRoute(String departureCity, String arrivalCity, String date) {
        ///TODO: Find expeditions by instant and route logic to be implemented.
        return null;
    }

    public List<ExpeditionForCompanyDTO> findExpeditionsByInstant(String date) {
        ///TODO: Find expeditions by instant logic to be implemented.
        return null;
    }

    public List<ExpeditionForCompanyDTO> findUpcomingExpeditions(int companyId) {
        ///TODO: Find upcoming expeditions logic to be implemented.
        return null;
    }

    public List<ExpeditionForCompanyDTO> findAllExpeditions(int companyId) {
        ///TODO: Find all expeditions logic to be implemented.
        return null;
    }

    public boolean doesExpeditionExist(int expeditionId) {
        ///TODO: Check if expedition exists logic to be implemented.
        return false;
    }
}
