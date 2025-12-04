package com.shubilet.expedition_service.services.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCustomerDTO;
import com.shubilet.expedition_service.services.SeatService;

@Service
public class SeatServiceImpl implements SeatService {
    
    public void generateSeats(long expeditionId, int capacity) {
        // TODO: Implement seat generation logic
    }

    public List<SeatForCustomerDTO> getAvailableSeats(int expeditionId) {
        ///TODO: Implement logic to fetch available seats
        return null;
    }

    public List<SeatForCompanyDTO> getSeatsByExpeditionId(int expeditionId) {
        ///TODO: Implement logic to fetch seats by expedition ID
        return null;
    }
}