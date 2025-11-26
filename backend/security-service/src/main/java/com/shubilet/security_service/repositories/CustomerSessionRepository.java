package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CustomerSession;


@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Integer> {
   
    ///TODO: Yorum satırları eklenecek.

    @Query("""
        SELECT COUNT(a) > 0
        FROM Customer a
        WHERE a.email = :email
            AND a.password = :password
    """)
    boolean isEmailAndPasswordValid(@Param("email") String email, @Param("password") String password);

    @Query("""
        SELECT a.id
        FROM Customer a
        WHERE a.email = :email
    """)
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
    boolean existsByCustomerIdAndCode( @Param("customerId") int customerId, @Param("code") String code);

    @Query("""
        SELECT COUNT(a) > 0
        FROM Customer a
        WHERE a.email = :email
    """)
    boolean hasEmail(@Param("email") String email);
}
