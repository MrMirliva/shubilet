package com.mirliva.member_service.services.Impl;

import org.springframework.stereotype.Service;

import com.mirliva.member_service.models.Customer;
import com.mirliva.member_service.repositories.CustomerRepository;
import com.mirliva.member_service.services.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    ///TODO: Just a test method, remove later.
    public void saveCustomer(Customer customer) {
        customerRepository.save(customer);
    }
}
