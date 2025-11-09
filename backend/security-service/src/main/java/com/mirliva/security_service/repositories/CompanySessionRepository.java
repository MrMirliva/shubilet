package com.mirliva.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mirliva.security_service.models.CompanySession;


@Repository
public interface CompanySessionRepository extends JpaRepository<CompanySession, Integer> {
    
}
