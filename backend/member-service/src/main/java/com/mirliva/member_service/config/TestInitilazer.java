package com.mirliva.member_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mirliva.member_service.services.CustomerService;

@Component
public class TestInitilazer implements CommandLineRunner{

    private final CustomerService customerService;

    public TestInitilazer(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Initialization completed.");
    }
}