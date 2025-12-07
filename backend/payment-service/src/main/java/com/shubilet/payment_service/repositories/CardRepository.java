package com.shubilet.payment_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shubilet.payment_service.models.Card;

import java.util.List;
import java.util.Optional;

/**

    Repository interface for Card entities. Extends Spring Data JPA's
    JpaRepository to provide basic CRUD operations and custom query
    methods for card management.

 */
@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    List<Card> findByCustomerId(Integer customerId);

    Optional<Card> findByIdAndCustomerId(Integer cardId, Integer customerId);
}

