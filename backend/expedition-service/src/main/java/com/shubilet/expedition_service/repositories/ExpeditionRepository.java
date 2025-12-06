package com.shubilet.expedition_service.repositories;

import java.util.List;
import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCompanyDTO;
import com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCustomerDTO;
import com.shubilet.expedition_service.models.Expedition;


@Repository
public interface ExpeditionRepository extends JpaRepository<Expedition, Integer> {

    @Query("""
        select new com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCompanyDTO(
            e.id,
            dc.name,
            ac.name,
            FUNCTION('to_char', e.dateAndTime, 'YYYY-MM-DD'),
            FUNCTION('to_char', e.dateAndTime, 'HH24:MI'),
            cast(e.price as double),
            e.duration,
            0,
            0,
            0.0
        )
        from Expedition e, City dc, City ac
        where e.companyId = :companyId
            and dc.id = e.departureCityId
            and ac.id = e.arrivalCityId
        order by e.dateAndTime asc
        """)
    List<ExpeditionForCompanyDTO> findAllByCompanyId(@Param("companyId") int companyId);


    @Query("""
        select new com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCompanyDTO(
            e.id,
            dc.name,
            ac.name,
            FUNCTION('to_char', e.dateAndTime, 'YYYY-MM-DD'),
            FUNCTION('to_char', e.dateAndTime, 'HH24:MI'),
            cast(e.price as double),
            e.duration,
            0,
            0,
            0.0
        )
        from Expedition e, City dc, City ac
        where e.companyId = :companyId
            and e.dateAndTime > :now
            and dc.id = e.departureCityId
            and ac.id = e.arrivalCityId
        order by e.dateAndTime asc
        """)
    List<ExpeditionForCompanyDTO> findUpcomingExpeditions(
            @Param("companyId") int companyId,
            @Param("now") Instant now
    );


    @Query("""
        select new com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCompanyDTO(
            e.id,
            dc.name,
            ac.name,
            FUNCTION('to_char', e.dateAndTime, 'YYYY-MM-DD'),
            FUNCTION('to_char', e.dateAndTime, 'HH24:MI'),
            cast(e.price as double),
            e.duration,
            0,
            0,
            0.0
        )
        from Expedition e, City dc, City ac
        where FUNCTION('date', e.dateAndTime) = FUNCTION('date', :instantDate)
            and dc.id = e.departureCityId
            and ac.id = e.arrivalCityId
        order by e.dateAndTime asc
        """)
    List<ExpeditionForCompanyDTO> findAllByInstant(@Param("instantDate") Instant instantDate);



    @Query("""
        select new com.shubilet.expedition_service.dataTransferObjects.responses.ExpeditionForCustomerDTO(
            e.id,
            dc.name,
            ac.name,
            FUNCTION('to_char', e.dateAndTime, 'YYYY-MM-DD'),
            FUNCTION('to_char', e.dateAndTime, 'HH24:MI'),
            e.price,
            e.duration,
            comp.name
        )
        from Expedition e, City dc, City ac, Company comp
        where e.departureCityId = :departureCityId
            and e.arrivalCityId = :arrivalCityId
            and FUNCTION('date', e.dateAndTime) = FUNCTION('date', :instantDate)
            and dc.id = e.departureCityId
            and ac.id = e.arrivalCityId
            and comp.id = e.companyId
        order by e.dateAndTime asc
        """)
    List<ExpeditionForCustomerDTO> findByInstantAndRoute(
        @Param("departureCityId") int departureCityId,
        @Param("arrivalCityId") int arrivalCityId,
        @Param("instantDate") Instant instantDate
    );



}