package com.shubilet.expedition_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.models.Seat;


@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
    
}
