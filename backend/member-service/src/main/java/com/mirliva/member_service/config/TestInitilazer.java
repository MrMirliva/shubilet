package com.mirliva.member_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mirliva.member_service.models.Customer;
import com.mirliva.member_service.services.CustomerService;

///TODO: Remove after tests
@Component
public class TestInitilazer implements CommandLineRunner{

    private final CustomerService customerService;

    public TestInitilazer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void run(String... args) throws Exception {

        Customer customer1 = new Customer("Jhon", "Doe", Customer.Gender.MALE, "admin@example.com", "securePassword123");

        customerService.saveCustomer(customer1);

        System.out.println("Initialization completed.");
    }
}