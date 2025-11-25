package com.shubilet.member_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shubilet.member_service.models.Company;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    
}
