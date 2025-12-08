package com.shubilet.security_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CustomerSession;

import jakarta.transaction.Transactional;

@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Integer> {

    /**

        Operation: Lookup

        Determines whether a customer session exists that uses the specified session code.
        This query checks the persistence layer to ensure uniqueness of session codes,
        typically during session creation workflows to avoid collisions with existing
        sessions.

        <p>

            Uses:

            <ul>
                <li>JPA query for session code existence checking</li>
            </ul>

        </p>

        @param code the session code whose existence is being checked

        @return {@code true} if a session with the given code exists, otherwise {@code false}
    */
    @Query("""
        SELECT COUNT(s) > 0
        FROM CustomerSession s
        WHERE s.code = :code
    """)
    boolean hasCode(@Param("code") String code);


    @Query("""
        select count(a) > 0
        from CustomerSession a
        where a.customerId = :customerId
    """)
    boolean existsByCustomerId(@Param("customerId") int customerId);

    /**

        Operation: Lookup

        Checks whether a customer session exists that matches both the specified customer
        identifier and session code. This operation is commonly used during session
        validation workflows to ensure that the provided token belongs to the correct
        customer.

        <p>

            Uses:

            <ul>
                <li>JPA query for session existence verification</li>
            </ul>

        </p>

        @param customerId the identifier of the customer to whom the session should belong

        @param code the session code associated with the session

        @return {@code true} if a matching session exists, otherwise {@code false}
    */
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

    /**

        Operation: Validate

        Determines whether a customer session has expired by checking for a matching session
        record associated with the given customer identifier and session code, and confirming
        that its expiration timestamp is less than or equal to the current system time.
        This validation is typically used during session authentication to reject stale or
        invalid session tokens.

        <p>

            Uses:

            <ul>
                <li>JPA query for session expiration evaluation</li>
            </ul>

        </p>

        @param customerId the identifier of the customer whose session is being evaluated

        @param code the session code associated with the session

        @return {@code true} if the session has expired, otherwise {@code false}
    */
    @Query("""
        SELECT COUNT(s) > 0
        FROM CustomerSession s
        WHERE s.customerId = :customerId
            AND s.code = :code
            AND s.expiresAt <= CURRENT_TIMESTAMP
    """)
    boolean isExpired(@Param("customerId") int customerId, @Param("code") String code);

    /**

        Operation: Cleanup

        Deletes all customer session records whose expiration timestamps have already passed.
        This bulk cleanup operation helps maintain data integrity and prevents the persistence
        of stale or invalid session entries in the underlying {@code customer_sessions} table.
        It is commonly triggered by scheduled maintenance tasks or administrative actions.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for removing expired session records</li>
                <li>Spring Transaction management for ensuring atomic execution</li>
            </ul>

        </p>

    */
    @Modifying
    @Transactional
    @Query(
        value = """
                DELETE FROM customer_sessions
                WHERE expires_at < NOW()
                """,
        nativeQuery = true
    )
    void deleteExpiredSessions();

    ///TODO: Add Query method to find CustomerSession by customerId
    @Transactional
    int deleteByCustomerIdAndCode(int customerId, String code);

    Optional<CustomerSession> findByCustomerIdAndCode(int customerId, String code);
}
