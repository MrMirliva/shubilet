package com.shubilet.expedition_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shubilet.expedition_service.models.Ticket;


@Repository
public interface TicketRepository extends JpaRepository<Ticket, String> {
    
}
