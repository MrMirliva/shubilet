package com.shubilet.expedition_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.models.City;


@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN TRUE ELSE FALSE END
        FROM City c
        WHERE c.name = :name
    """)
    boolean existsByName(@Param("name") String name);

    @Query("""
        SELECT COALESCE(
                (
                    SELECT c.id 
                    FROM City c 
                    WHERE c.name = :name
                ),
                -1
            )
    """)
    int findIdByName(@Param("name") String name);
}
