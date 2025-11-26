package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CompanySession;

@Repository
public interface CompanySessionRepository extends JpaRepository<CompanySession, Integer> {
    
    ///TODO: Yorum satırları eklenecek.

    @Query("""
        SELECT COUNT(a) > 0
        FROM Company a
        WHERE a.email = :email
            AND a.password = :password
    """)
    boolean isEmailAndPasswordValid(@Param("email") String email, @Param("password") String password);

    @Query("""
        SELECT a.id
        FROM Company a
        WHERE a.email = :email
    """)
    int getCompanyIdByEmail(@Param("email") String email);

    @Query("""
        SELECT COUNT(s) > 0
        FROM CompanySession s
        WHERE s.code = :code
    """)
    boolean hasCode(@Param("code") String code);

    @Query("""
        SELECT COUNT(s) > 0
        FROM CompanySession s
        WHERE s.companyId = :companyId
            AND s.code = :code
    """)
    boolean existsByCompanyIdAndCode( @Param("companyId") int companyId, @Param("code") String code);
    
    @Query("""
        SELECT COUNT(a) > 0
        FROM Company a
        WHERE a.email = :email
    """)
    boolean hasEmail(@Param("email") String email);


    ///TODO: ileride değişebilir.
    @Query("""
        SELECT COUNT(a) > 0
        FROM Company a
        WHERE a.email = :email
            AND a.refAdminId IS NOT NULL
            AND a.refAdminId > 0
    """)
    boolean isVerifiedEmail(@Param("email") String email);
}
