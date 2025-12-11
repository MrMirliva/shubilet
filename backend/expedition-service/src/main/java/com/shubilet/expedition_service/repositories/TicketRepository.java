package com.shubilet.expedition_service.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.TicketRepoDTO;
import com.shubilet.expedition_service.models.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    

    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.TicketRepoDTO(
            t.PNR,
            s.seatNo,
            e.id,
            e.companyId,
            dc.name,
            ac.name,
            e.dateAndTime,
            e.duration
        )
        FROM Ticket t
            JOIN Seat s ON t.seatId = s.id
            JOIN Expedition e ON s.expeditionId = e.id
            JOIN City dc ON e.departureCityId = dc.id
            JOIN City ac ON e.arrivalCityId = ac.id
        WHERE t.PNR = :PNR
        """
    )
    TicketRepoDTO findTicketDetailsByPNR(
        @Param("PNR") String PNR
    );

    @Query("""
        SELECT CASE 
            WHEN COUNT(t) > 0 THEN true
            ELSE false
        END
        FROM Ticket t
        WHERE t.PNR = :PNR
        """)
    boolean existsByPNR(String PNR);

    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.TicketRepoDTO(
            t.PNR,
            s.seatNo,
            e.id,
            e.companyId,
            dc.name,
            ac.name,
            e.dateAndTime,
            e.duration
        )
        FROM Ticket t
            JOIN Seat s ON t.seatId = s.id
            JOIN Expedition e ON s.expeditionId = e.id
            JOIN City dc ON e.departureCityId = dc.id
            JOIN City ac ON e.arrivalCityId = ac.id
        WHERE t.customerId = :customerId
        """
    )
    List<TicketRepoDTO> findTicketsByCustomerId(int customerId);
}
