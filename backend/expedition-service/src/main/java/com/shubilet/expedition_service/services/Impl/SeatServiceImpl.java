package com.shubilet.expedition_service.services.Impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCustomerDTO;
import com.shubilet.expedition_service.models.Seat;
import com.shubilet.expedition_service.services.SeatService;
import com.shubilet.expedition_service.repositories.SeatRepository;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImpl(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }
    
    public void generateSeats(int expeditionId, int capacity) {
        for(int i = 0; i < capacity; i++) {
            Seat seat = new Seat(expeditionId, i + 1);
            seatRepository.save(seat);
        }
    }

    public List<SeatForCustomerDTO> getAvailableSeats(int expeditionId) {
        return  seatRepository.findSeatsByExpeditionIdForCustomer(expeditionId);
    }

    public List<SeatForCompanyDTO> getSeatsByExpeditionId(int expeditionId) {
        return seatRepository.findSeatsByExpeditionIdForCompany(expeditionId);
    }
}