package com.shubilet.security_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.AdminSession;

import jakarta.transaction.Transactional;

@Repository
public interface AdminSessionRepository extends JpaRepository<AdminSession, Integer> {

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

    @Query("""
        select count(a) > 0
        from AdminSession a
        where a.adminId = :adminId
    """)
    boolean existsByAdminId(@Param("adminId") int adminId);


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

    ///TODO: Add Query method to find AdminSession by adminId
    Optional<AdminSession> findByAdminId(int adminId);
}
