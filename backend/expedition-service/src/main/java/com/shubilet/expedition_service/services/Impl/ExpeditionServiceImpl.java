package com.shubilet.expedition_service.services.Impl;

import java.time.Instant;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.ExpeditionForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.ExpeditionForCustomerDTO;
import com.shubilet.expedition_service.models.Expedition;
import com.shubilet.expedition_service.repositories.CityRepository;
import com.shubilet.expedition_service.repositories.ExpeditionRepository;
import com.shubilet.expedition_service.services.ExpeditionService;

@Service
public class ExpeditionServiceImpl implements ExpeditionService {

    private final ExpeditionRepository expeditionRepository;
    private final CityRepository cityRepository;

    public ExpeditionServiceImpl(
        ExpeditionRepository expeditionRepository,
        CityRepository cityRepository
    ) {
        this.expeditionRepository = expeditionRepository;
        this.cityRepository = cityRepository;
    }

    public int createExpedition(int companyId, String departureCity, String arrivalCity, String date, String time, int capacity, double price, int duration) {
        int departureCityId = cityRepository.findIdByName(departureCity);
        int arrivalCityId = cityRepository.findIdByName(arrivalCity);

        if(departureCityId == -1 || arrivalCityId == -1) {
            return -1;
        }

        Instant instantDate = Instant.parse(date + "T" + time + "Z");

        Expedition expedition = new Expedition(
            departureCityId,
            arrivalCityId,
            instantDate,
            BigDecimal.valueOf(price),
            duration,
            capacity,
            companyId
        );

        expeditionRepository.save(expedition);

        return expedition.getId();
    }

    public List<ExpeditionForCustomerDTO> findExpeditionsByInstantAndRoute(String departureCity, String arrivalCity, String date) {
        int departureCityId = cityRepository.findIdByName(departureCity);
        int arrivalCityId = cityRepository.findIdByName(arrivalCity);

        if(departureCityId == -1 || arrivalCityId == -1) {
            return List.of();
        }

        Instant instantDate = Instant.parse(date);
        Instant endOfDay = instantDate.plusSeconds(86399); // Add 23 hours, 59 minutes, and 59 seconds to get the end of the day
        return expeditionRepository.findByInstantAndRoute(departureCityId, arrivalCityId, instantDate, endOfDay);
    }

    public List<ExpeditionForCompanyDTO> findExpeditionsByInstant(String date) {
        Instant instantDate = Instant.parse(date);
        Instant endOfDay = instantDate.plusSeconds(86399); // Add 23 hours, 59 minutes, and 59 seconds to get the end of the day
        return expeditionRepository.findAllByInstant(instantDate, endOfDay);
    }

    public List<ExpeditionForCompanyDTO> findUpcomingExpeditions(int companyId) {
        Instant now = Instant.now();
        return expeditionRepository.findUpcomingExpeditions(companyId, now);
    }

    public List<ExpeditionForCompanyDTO> findAllExpeditions(int companyId) {
        return expeditionRepository.findAllByCompanyId(companyId);
    }

    public boolean expeditionExists(int expeditionId) {
        return expeditionRepository.existsById(expeditionId);
    }
    
}
