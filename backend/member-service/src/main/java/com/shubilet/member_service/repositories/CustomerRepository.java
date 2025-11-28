package com.shubilet.member_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shubilet.member_service.models.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query(
            value = """
                    SELECT COUNT(*) > 0
                    FROM customers c
                    WHERE c.email = :email;
                    """,
            nativeQuery = true
    )
    boolean isCustomerExistsByEmail(
            @Param("email") String email
    );
}
