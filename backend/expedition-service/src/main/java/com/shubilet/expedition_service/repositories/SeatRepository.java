package com.shubilet.expedition_service.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.SeatForCompanyRepoDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.SeatForCustomerRepoDTO;
import com.shubilet.expedition_service.models.Seat;


@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    
    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.SeatForCompanyRepoDTO(
            s.id,
            s.expeditionId,
            s.seatNo,
            s.customerId,
            s.status
        )
        FROM Seat s
            JOIN Expedition e
                ON s.expeditionId = e.id
                    WHERE s.expeditionId = :expeditionId
                        AND e.companyId = :companyId
        ORDER BY s.seatNo ASC
    """)
    List<SeatForCompanyRepoDTO> findSeatsByExpeditionIdAndCompanyId(
        @Param("expeditionId") int expeditionId, 
        @Param("companyId") int companyId
    );

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
            WHERE  s.seatNo = :seatNo
                AND s.expeditionId = :expeditionId
        """)
    Seat findByExpeditionIdAndSeatNo(
            @Param("expeditionId") int expeditionId,
            @Param("seatNo") int seatNo
    );

    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.SeatForCustomerRepoDTO(
            s.expeditionId,
            s.seatNo,
            s.status
        )
        FROM Seat s
        WHERE s.expeditionId IN (
                SELECT e.id 
                FROM Expedition e 
                WHERE e.dateAndTime >= :now
                    AND e.capacity > e.numberOfBookedSeats
            )
            AND s.expeditionId = :expeditionId
        ORDER BY s.seatNo ASC
    """)
    List<SeatForCustomerRepoDTO> findSeatsByExpeditionIdAndStatus(
            @Param("expeditionId") int expeditionId,
            @Param("now") Instant now
    );

}
