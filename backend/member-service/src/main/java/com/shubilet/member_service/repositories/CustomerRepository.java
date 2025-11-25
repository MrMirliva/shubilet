package com.shubilet.member_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shubilet.member_service.models.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
}
