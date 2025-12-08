package com.shubilet.expedition_service.services;

import java.util.List;

import com.shubilet.expedition_service.common.enums.BookStatus;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCustomerDTO;

public interface SeatService {
    
    public void generateSeats(int expeditionId, int capacity);

    public List<SeatForCustomerDTO> getAvailableSeats(int expeditionId);

    public List<SeatForCompanyDTO> getSeatsByExpeditionId(int expeditionId);

    public boolean seatExist(int expeditionId, int seatNo);

    public BookStatus bookSeat(int expeditionId, int seatNo);
}
