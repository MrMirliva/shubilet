package com.shubilet.security_service.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CompanySession;

import jakarta.transaction.Transactional;

@Repository
public interface CompanySessionRepository extends JpaRepository<CompanySession, Integer> {

    /**

        Operation: Lookup

        Determines whether a company session exists that uses the specified session code.
        This lookup is typically performed during session creation to ensure code
        uniqueness and prevent collisions with existing session identifiers.

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
        FROM CompanySession s
        WHERE s.code = :code
    """)
    boolean hasCode(@Param("code") String code);

    @Query("""
        select count(a) > 0
        from CompanySession a
        where a.companyId = :companyId
    """)
    boolean existsByCompanyId(@Param("companyId") int companyId);

    /**

        Operation: Lookup

        Checks whether a company session exists that matches both the provided company
        identifier and session code. This operation is commonly used during session
        validation workflows to ensure that the session token belongs to the correct
        company account.

        <p>

            Uses:

            <ul>
                <li>JPA query for session existence verification</li>
            </ul>

        </p>

        @param companyId the identifier of the company to whom the session should belong

        @param code the session code associated with the session

        @return {@code true} if a matching session exists, otherwise {@code false}
    */
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

    /**

        Operation: Validate

        Determines whether a specific company session has expired by checking for a session
        record that matches both the provided company identifier and session code, and
        verifying that its expiration timestamp is less than or equal to the current time.
        This operation is typically used during authentication and session validation workflows
        to ensure that expired or invalid sessions are appropriately rejected.

        <p>

            Uses:

            <ul>
                <li>JPA query for session expiration evaluation</li>
            </ul>

        </p>

        @param companyId the identifier of the company whose session is being evaluated

        @param code the session code associated with the session

        @return {@code true} if the session has expired, otherwise {@code false}
    */
    @Query("""
        SELECT COUNT(s) > 0
        FROM CompanySession s
        WHERE s.companyId = :companyId
            AND s.code = :code
            AND s.expiresAt <= CURRENT_TIMESTAMP
    """)
    boolean isExpired(@Param("companyId") int companyId, @Param("code") String code);

    /**

        Operation: Cleanup

        Removes all company session records whose expiration timestamps have already passed.
        This bulk deletion helps maintain data integrity and prevents accumulation of stale
        session entries in the underlying {@code company_sessions} table. It is typically
        used in scheduled maintenance tasks or administrative cleanup workflows.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for deleting expired session records</li>
                <li>Spring Transaction management for atomic operation execution</li>
            </ul>

        </p>

    */
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

    ///TODO: Add Query method to find CompanySession by companyId
    @Transactional
    int deleteByCompanyIdAndCode(int companyId, String code);

    Optional<CompanySession> findByCompanyIdAndCode(int companyId, String code);
}
