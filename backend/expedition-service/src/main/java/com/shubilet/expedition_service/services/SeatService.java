package com.shubilet.expedition_service.services;

import java.util.List;

import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCustomerDTO;

public interface SeatService {
    
    public void generateSeats(long expeditionId, int capacity);

    public List<SeatForCustomerDTO> getAvailableSeats(int expeditionId);

    public List<SeatForCompanyDTO> getSeatsByExpeditionId(int expeditionId);
}
