package com.mirliva.payment_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mirliva.payment_service.models.Card;


@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {
    
}
