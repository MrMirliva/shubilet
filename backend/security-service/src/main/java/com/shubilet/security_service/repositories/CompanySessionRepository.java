package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CompanySession;

import jakarta.transaction.Transactional;

@Repository
public interface CompanySessionRepository extends JpaRepository<CompanySession, Integer> {
    
    ///TODO: Yorum satırları eklenecek.

    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM companies c
                WHERE c.email = :email
                    AND c.password = :password
                """,
        nativeQuery = true
    )
    boolean isEmailAndPasswordValid(
            @Param("email") String email,
            @Param("password") String password
    );


    @Query(
        value = """
                SELECT c.id
                FROM companies c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
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
    boolean existsByCompanyIdAndCode(
            @Param("companyId") int companyId, 
            @Param("code") String code
    );

    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM companies c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
    boolean hasEmail(@Param("email") String email);


    ///TODO: ileride değişebilir.
    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM companies c
                WHERE c.email = :email
                    AND c.ref_admin_id IS NOT NULL
                    AND c.ref_admin_id > 0
                """,
        nativeQuery = true
    )
    boolean isVerifiedEmail(@Param("email") String email);

    ///TODO: ileride değişebilir.
    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM companies c
                WHERE c.id = :companyId
                    AND c.ref_admin_id IS NOT NULL
                    AND c.ref_admin_id > 0
                """,
        nativeQuery = true
    )
    boolean isVerifiedCompany(@Param("companyId") int companyId);

    @Query("""
        SELECT COUNT(s) > 0
        FROM CompanySession s
        WHERE s.companyId = :companyId
            AND s.code = :code
            AND s.expiresAt <= CURRENT_TIMESTAMP
    """)
    boolean isExpired(@Param("companyId") int companyId, @Param("code") String code);

    @Modifying
    @Transactional
    @Query(
        value = """
                DELETE FROM company_sessions
                WHERE expires_at < NOW()
                """,
        nativeQuery = true
    )
    void deleteExpiredSessions();
}
