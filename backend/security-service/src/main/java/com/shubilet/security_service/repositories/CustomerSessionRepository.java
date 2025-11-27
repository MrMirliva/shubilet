package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CustomerSession;


@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Integer> {

    ///TODO: Yorum satırları eklenecek.

    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM customers c
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
                FROM customers c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
    int getCustomerIdByEmail(@Param("email") String email);

    @Query("""
        SELECT COUNT(s) > 0
        FROM CustomerSession s
        WHERE s.code = :code
    """)
    boolean hasCode(@Param("code") String code);

    @Query("""
        SELECT COUNT(s) > 0
        FROM CustomerSession s
        WHERE s.customerId = :customerId
            AND s.code = :code
    """)
    boolean existsByCustomerIdAndCode(
            @Param("customerId") int customerId, 
            @Param("code") String code
    );

    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM customers c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
    boolean hasEmail(@Param("email") String email);

    @Query("""
        SELECT COUNT(s) > 0
        FROM CustomerSession s
        WHERE s.customerId = :customerId
            AND s.code = :code
            AND s.expiresAt <= CURRENT_TIMESTAMP
    """)
    boolean isExpired(@Param("customerId") int customerId, @Param("code") String code);
}
