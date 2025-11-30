package com.shubilet.security_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.security_service.models.CompanySession;

import jakarta.transaction.Transactional;

/**

    Domain: Persistence

    Defines the persistence-layer contract for managing {@code CompanySession} entities,
    combining generic CRUD support with specialized queries that facilitate company
    authentication and session lifecycle operations. By leveraging Spring Data JPA,
    this repository encapsulates both JPQL and native SQL-based lookups for credential
    validation, email and verification checks, session existence and expiration
    evaluation, as well as bulk cleanup of expired session records in the underlying
    data store. It serves as the primary gateway for all session-related persistence
    concerns for company users.

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

    @see com.shubilet.security_service.models.CompanySession

    @see com.shubilet.security_service.services.CompanySessionService

    @author Abdullah (Mirliva) GÜNDÜZ - https://github.com/MrMilriva

    @version 1.0
*/
@Repository
public interface CompanySessionRepository extends JpaRepository<CompanySession, Integer> {
    
    /**

        Operation: Validate

        Checks whether a company account exists that matches the provided email and password.
        This database-level verification performs a direct lookup against the underlying
        {@code companies} table to authenticate company users. The query returns a boolean
        indicating whether such a matching record is present.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for credential validation</li>
            </ul>

        </p>

        @param email the company's email used for authentication

        @param password the company's password used for authentication

        @return {@code true} if a matching company record exists, otherwise {@code false}
    */
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

    /**

        Operation: Lookup

        Retrieves the unique identifier of a company associated with the specified email.
        This operation performs a direct query against the underlying {@code companies}
        table and returns the primary key of the matching company record. It is commonly
        used during authentication and session initialization workflows.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for company ID retrieval</li>
            </ul>

        </p>

        @param email the email address whose corresponding company ID is requested

        @return the unique identifier of the company associated with the given email
    */
    @Query(
        value = """
                SELECT c.id
                FROM companies c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
    int getCompanyIdByEmail(@Param("email") String email);

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

        Operation: Lookup

        Determines whether a company account exists with the specified email address.
        This lookup is typically used during authentication flows, registration checks,
        and account recovery processes by querying the underlying {@code companies} table.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for email existence checking</li>
            </ul>

        </p>

        @param email the email address to verify

        @return {@code true} if a company with the given email exists, otherwise {@code false}
    */
    @Query(
        value = """
                SELECT COUNT(*) > 0
                FROM companies c
                WHERE c.email = :email
                """,
        nativeQuery = true
    )
    boolean hasEmail(@Param("email") String email);

/**

        Operation: Validate

        Determines whether the specified company email belongs to a verified company account.
        Verification is confirmed by checking whether the {@code ref_admin_id} field in the
        underlying {@code companies} table is not null and contains a positive value.
        This operation supports workflows requiring confirmation of a company's verified
        status before granting access or performing sensitive operations.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for company verification checking</li>
            </ul>

        </p>

        @param email the email address whose verification status is being evaluated

        @return {@code true} if the company account is verified, otherwise {@code false}
    */
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

    /**

        Operation: Validate

        Checks whether the company associated with the specified identifier is verified.
        Verification is determined by inspecting the {@code ref_admin_id} field in the
        underlying {@code companies} table and ensuring that it contains a non-null,
        positive reference. This operation is commonly used during authentication,
        authorization, and session validation processes.

        <p>

            Uses:

            <ul>
                <li>Native SQL query for company verification checking</li>
            </ul>

        </p>

        @param companyId the identifier of the company whose verification status is being evaluated

        @return {@code true} if the company is verified, otherwise {@code false}
    */
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
}
