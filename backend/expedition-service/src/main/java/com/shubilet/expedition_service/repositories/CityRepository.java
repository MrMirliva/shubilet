package com.shubilet.expedition_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.models.City;


@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    @Query(
        value = 
            """
                SELECT COUNT(*) > 0
                FROM cities
                WHERE name = :name
            """,
        nativeQuery = true
    )
    boolean existsByName(@Param("name") String name);

    @Query(
        value = 
            """
                SELECT id
                FROM cities
                WHERE name = :name
            """,
        nativeQuery = true
    )
    int findIdByName(@Param("name") String name);}
