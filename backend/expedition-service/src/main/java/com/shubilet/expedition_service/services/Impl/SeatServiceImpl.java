package com.shubilet.expedition_service.services.Impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.shubilet.expedition_service.common.enums.BookStatus;
import com.shubilet.expedition_service.common.enums.SeatStatus;
import com.shubilet.expedition_service.common.util.DTOMapperUtils;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCustomerDTO;
import com.shubilet.expedition_service.models.Seat;
import com.shubilet.expedition_service.services.SeatService;
import com.shubilet.expedition_service.repositories.SeatRepository;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;

    public SeatServiceImpl(
        SeatRepository seatRepository
    ) {
        this.seatRepository = seatRepository;
    }
    
    public void generateSeats(int expeditionId, int capacity) {
        for(int i = 0; i < capacity; i++) {
            Seat seat = new Seat(expeditionId, i + 1);
            seatRepository.save(seat);
        }
    }

    public List<SeatForCustomerDTO> getAvailableSeats(int expeditionId) {
        return DTOMapperUtils.toSeatForCustomerDTO(seatRepository.findSeatsByExpeditionIdAndStatus(expeditionId, SeatStatus.AVAILABLE));
        
    }

    public List<SeatForCompanyDTO> getSeatsByExpeditionId(int expeditionId) {
        return DTOMapperUtils.toSeatForCompanyDTO(
            seatRepository.findSeatsByExpeditionIdForCompany(
                expeditionId
            )
        );
    }

    public boolean seatExist(int expeditionId, int seatNo) {
        return seatRepository.existsByExpeditionIdAndSeatNo(expeditionId, seatNo);
    }

    public BookStatus bookSeat(int expeditionId, int seatNo) {

        Seat seat = seatRepository.findByExpeditionIdAndSeatNo(expeditionId, seatNo);

        if(seat == null) {
            return BookStatus.SEAT_NOT_EXISTS;
        }

        if(seat.isBooked()) {
            return BookStatus.ALREADY_BOOKED;
        }

        seat.setBooked(true);
        seatRepository.save(seat);
        
        return BookStatus.SUCCESS;
    }
}