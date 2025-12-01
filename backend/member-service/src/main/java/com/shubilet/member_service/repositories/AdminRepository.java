package com.shubilet.member_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.member_service.models.Admin;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    @Query(
            value = """
                    SELECT COUNT(*) > 0
                    FROM admins a
                    WHERE a.email = :email;
                    """,
            nativeQuery = true
    )
    boolean isAdminExistsByEmail(
            @Param("email") String email
    );
    
}