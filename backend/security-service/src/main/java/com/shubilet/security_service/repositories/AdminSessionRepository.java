package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.AdminSession;


@Repository
public interface AdminSessionRepository extends JpaRepository<AdminSession, Integer> {
    
    ///TODO: Yorum satırları eklenecek.

    @Query("""
        SELECT COUNT(a) > 0
        FROM Admin a
        WHERE a.email = :email
            AND a.password = :password
    """)
    boolean isEmailAndPasswordValid(@Param("email") String email, @Param("password") String password);

    @Query("""
        SELECT a.id
        FROM Admin a
        WHERE a.email = :email
    """)
    int getAdminIdByEmail(@Param("email") String email);

    @Query("""
        SELECT COUNT(s) > 0
        FROM AdminSession s
        WHERE s.code = :code
    """)
    boolean hasCode(@Param("code") String code);

    @Query("""
        SELECT COUNT(s) > 0
        FROM AdminSession s
        WHERE s.adminId = :adminId
            AND s.code = :code
    """)
    boolean existsByAdminIdAndCode( @Param("adminId") int adminId, @Param("code") String code);

    @Query("""
        SELECT COUNT(a) > 0
        FROM Admin a
        WHERE a.email = :email
    """)
    boolean hasEmail(@Param("email") String email);


    ///TODO: ileride değişebilir.
    @Query("""
        SELECT COUNT(a) > 0
        FROM Admin a
        WHERE a.email = :email
            AND a.refAdminId IS NOT NULL
            AND a.refAdminId > 0
    """)
    boolean isVerifiedEmail(@Param("email") String email);
}
