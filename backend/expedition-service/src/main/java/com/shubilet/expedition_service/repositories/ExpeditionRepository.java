package com.shubilet.expedition_service.repositories;

import java.util.List;
import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.ExpeditionForCompanyRepoDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.ExpeditionForCustomerRepoDTO;
import com.shubilet.expedition_service.models.Expedition;


@Repository
public interface ExpeditionRepository extends JpaRepository<Expedition, Integer> {

    @Query("""
    SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.ExpeditionForCompanyRepoDTO(
            e.id,
            dc.name,
            ac.name,
            e.dateAndTime,
            e.price,
            e.duration,
            e.capacity,
            e.numberOfBookedSeats,
            e.profit
        )
        FROM Expedition e
            JOIN City dc 
                ON e.departureCityId = dc.id
                    JOIN City ac 
                        ON e.arrivalCityId = ac.id
        WHERE e.companyId = :companyId
        ORDER BY e.dateAndTime ASC
    """)
    List<ExpeditionForCompanyRepoDTO> findAllByCompanyId(@Param("companyId") int companyId);

    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.ExpeditionForCompanyRepoDTO(
            e.id,
            dc.name,
            ac.name,
            e.dateAndTime,
            e.price,
            e.duration,
            e.capacity,
            e.numberOfBookedSeats,
            e.profit
        )
        FROM Expedition e
            JOIN City dc 
                ON e.departureCityId = dc.id
                    JOIN City ac 
                        ON e.arrivalCityId = ac.id
                            WHERE e.companyId = :companyId
                                AND e.dateAndTime >= :now
        ORDER BY e.dateAndTime ASC
    """)
    List<ExpeditionForCompanyRepoDTO> findUpcomingExpeditions(
            @Param("companyId") int companyId,
            @Param("now") Instant now
    );


    @Query("""
    SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.ExpeditionForCustomerRepoDTO(
            e.id,
            dc.name,
            ac.name,
            e.dateAndTime,
            e.price,
            e.duration,
            e.companyId
        )
        FROM Expedition e, City dc, City ac
        WHERE e.departureCityId = dc.id
            AND e.arrivalCityId = ac.id
            AND e.departureCityId = :departureCityId
            AND e.arrivalCityId = :arrivalCityId
            AND e.dateAndTime >= :startOfDay
            AND e.dateAndTime < :endOfDay
        ORDER BY e.dateAndTime ASC
    """)
    List<ExpeditionForCustomerRepoDTO> findByInstantAndRoute(
            @Param("departureCityId") int departureCityId,
            @Param("arrivalCityId") int arrivalCityId,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay
    );

    @Query("""
        SELECT new com.shubilet.expedition_service.dataTransferObjects.responses.forRepositories.ExpeditionForCompanyRepoDTO(
            e.id,
            dc.name,
            ac.name,
            e.dateAndTime,
            e.price,
            e.duration,
            e.capacity,
            e.numberOfBookedSeats,
            e.profit
        )
        FROM Expedition e
            JOIN City dc 
                ON e.departureCityId = dc.id
                    JOIN City ac 
                        ON e.arrivalCityId = ac.id
                            WHERE e.dateAndTime >= :startOfDay
                                AND e.dateAndTime < :endOfDay
        ORDER BY e.dateAndTime ASC
    """)
    List<ExpeditionForCompanyRepoDTO> findAllByInstant(
        @Param("startOfDay") Instant startOfDay, 
        @Param("endOfDay") Instant endOfDay
    );
}