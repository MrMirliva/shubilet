package com.shubilet.member_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.member_service.models.Company;


@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
    @Query(
            value = """
                    SELECT COUNT(*) > 0
                    FROM companies c
                    WHERE c.email = :email;
                    """,
            nativeQuery = true
    )
    boolean isCompanyExistsByEmail(
            @Param("email") String email
    );
    
}
