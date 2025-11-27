package com.shubilet.member_service.services.Impl;

import org.springframework.stereotype.Service;

import com.shubilet.member_service.models.Customer;
import com.shubilet.member_service.repositories.CustomerRepository;
import com.shubilet.member_service.services.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void createCustomer(Customer customer) {
        customerRepository.save(customer);
    }
}
