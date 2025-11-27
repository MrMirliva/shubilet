package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.AdminSession;


@Repository
public interface AdminSessionRepository extends JpaRepository<AdminSession, Integer> {
    
    ///TODO: Yorum satırları eklenecek.

    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM admins a
                WHERE a.email = :email
                    AND a.password = :password
                """,
        nativeQuery = true
    )
    boolean isEmailAndPasswordValid(
            @Param("email") String email,
            @Param("password") String password
    );


    @Query(
        value = """
                SELECT a.id
                FROM admins a
                WHERE a.email = :email
                """,
        nativeQuery = true
    )
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
    boolean existsByAdminIdAndCode(
            @Param("adminId") int adminId, 
            @Param("code") String code
    );

    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM admins a
                WHERE a.email = :email
                """,
        nativeQuery = true
    )
    boolean hasEmail(@Param("email") String email);


    ///TODO: ileride değişebilir.
    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM admins a
                WHERE a.email = :email
                    AND a.ref_admin_id IS NOT NULL
                    AND a.ref_admin_id > 0
                """,
        nativeQuery = true
    )
    boolean isVerifiedEmail(@Param("email") String email);

    ///TODO: ileride değişebilir.
    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM admins a
                WHERE a.id = :adminId
                    AND a.ref_admin_id IS NOT NULL
                    AND a.ref_admin_id > 0
                """,
        nativeQuery = true
    )
    boolean isVerifiedAdmin(@Param("adminId") int adminId);

    @Query("""
        SELECT COUNT(s) > 0
        FROM AdminSession s
        WHERE s.adminId = :adminId
            AND s.code = :code
            AND s.expiresAt <= CURRENT_TIMESTAMP
    """)
    boolean isExpired(@Param("adminId") int adminId, @Param("code") String code);
}
