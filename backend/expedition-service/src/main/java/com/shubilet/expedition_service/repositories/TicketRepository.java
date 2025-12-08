package com.shubilet.expedition_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO;
import com.shubilet.expedition_service.models.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    

    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.base.TicketDTO(
            t.PNR,
            s.seatNo,
            e.id,
            e.companyId,
            dc.name,
            ac.name,
            FUNCTION('to_char', e.dateAndTime, 'YYYY-MM-DD'),
            FUNCTION('to_char', e.dateAndTime, 'HH24:MI'),
            FUNCTION('to_char', e.duration, 'FM999999')
            )
        FROM Ticket t
            JOIN Seat s 
                ON t.seatId = s.id
                    JOIN Expedition e 
                        ON s.expeditionId = e.id
                            JOIN City dc 
                                ON e.departureCityId = dc.id
                                    JOIN City ac 
                                        ON e.arrivalCityId = ac.id
                                            WHERE e.id = :expeditionId
                                                AND s.seatNo = :seatNo
        """)
    TicketDTO findTicketDetailsByExpeditionIdAndSeatNo(
        @Param("expeditionId") int expeditionId,
        @Param("seatNo") int seatNo
    );

}
