package com.shubilet.payment_service.repositories;

import com.shubilet.payment_service.models.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Integer> {

    List<Card> findByCustomerIdAndIsActiveTrue(Integer customerId);
}
