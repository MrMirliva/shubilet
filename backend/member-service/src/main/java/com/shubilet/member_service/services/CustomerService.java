package com.shubilet.member_service.services;

import com.shubilet.member_service.models.Customer;

public interface CustomerService {
    /**
     * Creates a Customer on the Table with Validated Costumer Model
     *
     * @param customer Customer Model that going to be saved on DB
     * @return Returns True when operation is successful, false otherwise.
     */
    boolean createCustomer(Customer customer);

    /**
     * Checks that is there any user with given email
     *
     * @param email Email value to be checked
     * @return Returns True if there are any customer with given email, False otherwise.
     */
    boolean isCustomerExistsByEmail(String email);
}
