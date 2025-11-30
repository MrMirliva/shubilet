package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.AdminSession;

import jakarta.transaction.Transactional;

/**

    Domain: Persistence

    Defines the persistence-layer contract for managing {@code AdminSession} entities,
    providing both generic CRUD capabilities and specialized queries to support
    administrator authentication and session lifecycle management. By extending
    Spring Data JPA, this repository encapsulates JPQL and native SQL operations
    for credential checks, email and verification lookups, session existence and
    expiration validation, as well as bulk cleanup of expired session records in
    the underlying data store.

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

    @see com.shubilet.security_service.models.AdminSession

    @see com.shubilet.security_service.services.AdminSessionService

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
@Repository
public interface AdminSessionRepository extends JpaRepository<AdminSession, Integer> {
    

    /**

        Operation: Validate

        Checks whether an admin account exists that matches the provided email and password.
        This database-level validation is used to authenticate administrators by performing
        a direct lookup against the underlying admins table. The query returns a boolean
        indicating whether such a matching record is present.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for credential verification</li>
            </ul>

        </p>

        @param email the admin's email used for authentication

        @param password the admin's password used for authentication

        @return {@code true} if a matching admin record exists, otherwise {@code false}
    */
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


    /**

        Operation: Lookup

        Retrieves the unique identifier of an admin associated with the specified email.
        This query performs a direct lookup on the underlying admins table and returns
        the admin's primary key. It is typically used during authentication workflows and
        session creation processes following credential validation.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for admin ID retrieval</li>
            </ul>

        </p>

        @param email the email address whose corresponding admin ID is being requested

        @return the unique identifier of the admin associated with the given email
    */
    @Query(
        value = """
                SELECT a.id
                FROM admins a
                WHERE a.email = :email
                """,
        nativeQuery = true
    )
    int getAdminIdByEmail(@Param("email") String email);
    
    /**

        Operation: Lookup

        Determines whether an existing admin session uses the specified session code.
        This operation checks the persistence layer for code uniqueness and is typically
        used during session creation workflows to prevent collisions between generated
        session identifiers.

        <p>

            Uses:

            <ul>
                <li>JPA query for session code existence checking</li>
            </ul>

        </p>

        @param code the session code whose existence is being verified

        @return {@code true} if a session with the given code exists, otherwise {@code false}
    */
    @Query("""
        SELECT COUNT(s) > 0
        FROM AdminSession s
        WHERE s.code = :code
    """)
    boolean hasCode(@Param("code") String code);

    /**

        Operation: Lookup

        Checks whether an admin session exists that matches both the specified admin
        identifier and session code. This operation is commonly used during session
        validation workflows to ensure that the provided session token belongs to the
        correct administrator.

        <p>

            Uses:

            <ul>
                <li>JPA query for session existence verification</li>
            </ul>

        </p>

        @param adminId the identifier of the admin to whom the session should belong

        @param code the session code associated with the session

        @return {@code true} if a matching session exists, otherwise {@code false}
    */
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

    /**

        Operation: Lookup

        Determines whether an admin account exists with the specified email address.
        This operation performs a direct lookup on the underlying {@code admins} table
        to support validation workflows such as login, registration checks, and account
        recovery processes.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for email existence checking</li>
            </ul>

        </p>

        @param email the email address being checked for existence

        @return {@code true} if an admin with the given email exists, otherwise {@code false}
    */
    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM admins a
                WHERE a.email = :email
                """,
        nativeQuery = true
    )
    boolean hasEmail(@Param("email") String email);

    /**

        Operation: Validate

        Determines whether the specified admin email belongs to a verified administrator.
        Verification is confirmed by checking the presence and validity of the
        {@code ref_admin_id} field in the underlying {@code admins} table. This operation
        supports authentication and authorization workflows that require verified admin
        identities.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for checking admin verification status</li>
            </ul>

        </p>

        @param email the email address whose verification status is being evaluated

        @return {@code true} if the admin account is verified, otherwise {@code false}
    */
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

    /**

        Operation: Validate

        Checks whether the administrator associated with the specified identifier is
        verified. Verification is determined by inspecting the {@code ref_admin_id}
        field in the underlying {@code admins} table and ensuring that it contains a
        valid non-null, positive reference. This operation is typically used during
        authentication, authorization, and session validation workflows.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for admin verification checking</li>
            </ul>

        </p>

        @param adminId the identifier of the admin whose verification status is being evaluated

        @return {@code true} if the admin is verified, otherwise {@code false}
    */
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

    /**

        Operation: Validate

        Determines whether a specific admin session has expired by checking for a session
        record that matches both the provided admin identifier and session code while
        verifying that its expiration timestamp is less than or equal to the current time.
        This evaluation is typically used during authentication and session validation
        processes to ensure that stale or invalid sessions are rejected.

        <p>

            Uses:

            <ul>
                <li>JPA query for session expiration evaluation</li>
            </ul>

        </p>

        @param adminId the identifier of the admin whose session is being evaluated

        @param code the session code associated with the session

        @return {@code true} if the session has expired, otherwise {@code false}
    */
    @Query("""
        SELECT COUNT(s) > 0
        FROM AdminSession s
        WHERE s.adminId = :adminId
            AND s.code = :code
            AND s.expiresAt <= CURRENT_TIMESTAMP
    """)
    boolean isExpired(@Param("adminId") int adminId, @Param("code") String code);

    /**

        Operation: Cleanup

        Deletes all admin session records from the underlying storage whose expiration
        timestamps have passed. This operation is typically invoked as part of scheduled
        maintenance tasks or administrative cleanup workflows to remove stale or invalid
        session entries and preserve data integrity.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for bulk deletion of expired session records</li>
                <li>Spring Transaction management for ensuring atomic execution</li>
            </ul>

        </p>

    */
    @Modifying
    @Transactional
    @Query(
        value = """
                DELETE FROM admin_sessions
                WHERE expires_at < NOW()
                """,
        nativeQuery = true
    )
    void deleteExpiredSessions();
}
