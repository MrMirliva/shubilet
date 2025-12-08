package com.shubilet.expedition_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCustomerDTO;
import com.shubilet.expedition_service.models.Seat;


@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    
    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCompanyDTO(
            s.id,
            s.expeditionId,
            s.seatNo,
            s.customerId,
            s.status
        )
        FROM Seat s
        WHERE s.expeditionId = :expeditionId
        ORDER BY s.seatNo ASC
    """)
    List<SeatForCompanyDTO> findSeatsByExpeditionIdForCompany(@Param("expeditionId") int expeditionId);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
        FROM Seat s
        WHERE s.expeditionId = :expeditionId
            AND s.seatNo = :seatNo
    """)
    boolean existsByExpeditionIdAndSeatNo(
            @Param("expeditionId") int expeditionId,
            @Param("seatNo") int seatNo
    );

    @Query("""
        SELECT s
        FROM Seat s
        WHERE s.expeditionId = :expeditionId
            AND s.seatNo = :seatNo
        """)
    Seat findByExpeditionIdAndSeatNo(
            @Param("expeditionId") int expeditionId,
            @Param("seatNo") int seatNo
    );

    /*@Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.base.SeatForCustomerDTO(
            s.customerId,
            s.expeditionId,
            s.seatNo,
            s.status
        )
        FROM Seat s
            WHERE s.expeditionId = :expeditionId
                AND s.status = 'AVAILABLE'
    """)
    List<SeatForCustomerDTO> findAvailableSeatsByExpeditionId(@Param("expeditionId") int expeditionId);*/

    @Query("""
        SELECT s
        FROM Seat s
        WHERE s.expeditionId = :expeditionId
            AND s.status = com.shubilet.expedition_service.enums.Status.AVAILABLE
    """)
    List<Seat> findAvailableSeatsByExpeditionId(@Param("expeditionId") int expeditionId);
}
