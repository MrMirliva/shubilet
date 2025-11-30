package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CustomerSession;

import jakarta.transaction.Transactional;

/**

    Domain: Persistence

    Declares the persistence-layer contract for managing {@code CustomerSession}
    entities, combining standard CRUD functionality with specialized queries that
    support customer authentication and session lifecycle management. Through
    a mix of native SQL and JPQL-based operations, this repository enables
    credential checks, customer identifier lookups, session code uniqueness
    verification, existence and expiration validation, and bulk cleanup of
    expired session records. It serves as the primary integration point between
    the application’s customer session logic and the underlying database.

    <p>

        Technologies:

        <ul>
            <li>Spring Data JPA</li>
            <li>Spring Repository</li>
            <li>JPQL</li>
            <li>Native SQL</li>
            <li>Jakarta Transactions</li>
        </ul>

    </p>

    @see com.shubilet.security_service.models.CustomerSession

    @see com.shubilet.security_service.services.CustomerSessionService

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
@Repository
public interface CustomerSessionRepository extends JpaRepository<CustomerSession, Integer> {

    /**

        Operation: Validate

        Checks whether a customer account exists that matches the provided email and password.
        This validation is performed via a direct lookup against the underlying
        {@code customers} table to authenticate customer users. The query returns a boolean
        value indicating whether such a matching record exists.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for credential verification</li>
            </ul>

        </p>

        @param email the customer's email used for authentication

        @param password the customer's password used for authentication

        @return {@code true} if a matching customer record exists, otherwise {@code false}
    */
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

    /**

        Operation: Lookup

        Retrieves the unique identifier of a customer associated with the specified email.
        This operation queries the underlying {@code customers} table directly and returns
        the primary key of the matching customer record. It is commonly used during
        authentication and session initialization workflows following credential validation.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for customer ID retrieval</li>
            </ul>

        </p>

        @param email the email address whose corresponding customer ID is requested

        @return the unique identifier of the customer associated with the given email
    */
    @Query(
        value = """
                SELECT c.id
                FROM customers c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
    int getCustomerIdByEmail(@Param("email") String email);

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

        Operation: Lookup

        Checks whether a customer account exists with the specified email address. This
        operation performs a direct lookup on the underlying {@code customers} table and
        is typically used during authentication flows, registration checks, and account
        recovery processes.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for email existence lookup</li>
            </ul>

        </p>

        @param email the email address to check

        @return {@code true} if a customer with the given email exists, otherwise {@code false}
    */
    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM customers c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
    boolean hasEmail(@Param("email") String email);

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
}
