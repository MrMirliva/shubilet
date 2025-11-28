package com.shubilet.security_service.sweeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.shubilet.security_service.controllers.Impl.AuthControllerImpl;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.services.CompanySessionService;
import com.shubilet.security_service.services.CustomerSessionService;


@Component
public class StartupCleaner implements CommandLineRunner{
    private static final Logger logger = LoggerFactory.getLogger(AuthControllerImpl.class);

    private final AdminSessionService adminSessionService;
    private final CompanySessionService companySessionService;
    private final CustomerSessionService customerSessionService;

    public StartupCleaner(
        AdminSessionService adminSessionService, 
        CompanySessionService companySessionService, 
        CustomerSessionService customerSessionService
    ) {
        this.adminSessionService = adminSessionService;
        this.companySessionService = companySessionService;
        this.customerSessionService = customerSessionService;
    }

    ///TODO: Yorum satırları eklenecek
    ///TODO: Error handling eklenecek
    /// TODO: Loglama detaylandırılacak
    /// TODO: Test edilecek
    /// TODO: 
    @Override
    public void run(String... args) throws Exception {

        logger.info("Cleaning up all sessions on startup...");
        adminSessionService.cleanAllSessions();
        companySessionService.cleanAllSessions();
        customerSessionService.cleanAllSessions();
        logger.info("All sessions cleaned up.");
    }
}
