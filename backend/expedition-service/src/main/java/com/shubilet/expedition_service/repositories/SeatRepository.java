package com.shubilet.expedition_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCustomerDTO;
import com.shubilet.expedition_service.models.Seat;


@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    
    @Query("""
        select new com.shubilet.expedition_service.dataTransferObjects.responses.SeatForCustomerDTO(
            s.id,
            s.expeditionId,
            s.seatNo,
            s.status
        )
        from Seat s
        where s.expeditionId = :expeditionId
        order by s.seatNo asc
    """)
    List<SeatForCustomerDTO> findSeatsByExpeditionIdForCustomer(@Param("expeditionId") int expeditionId);

    @Query("""
        select new ccom.shubilet.expedition_service.dataTransferObjects.responses.SeatForCompanyDTO(
            s.id,
            s.expeditionId,
            s.seatNo,
            s.status,
            s.customerId
        )
        from Seat s
        where s.expeditionId = :expeditionId
        order by s.seatNo asc
    """)
    List<SeatForCompanyDTO> findSeatsByExpeditionIdForCompany(@Param("expeditionId") int expeditionId);
}
