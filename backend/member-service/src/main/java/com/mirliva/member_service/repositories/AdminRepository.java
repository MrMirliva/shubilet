package com.mirliva.member_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.mirliva.member_service.models.Admin;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    
}