package com.shubilet.member_service.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shubilet.member_service.services.RegistrationService;

@Component
public class TestInitilazer implements CommandLineRunner{

    private final RegistrationService registrationService;

    public TestInitilazer(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Initialization completed.");
    }
}