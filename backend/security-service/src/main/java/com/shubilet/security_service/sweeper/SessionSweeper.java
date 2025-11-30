package com.shubilet.security_service.sweeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shubilet.security_service.common.constants.AppConstants;
import com.shubilet.security_service.services.AdminSessionService;
import com.shubilet.security_service.services.CompanySessionService;
import com.shubilet.security_service.services.CustomerSessionService;

/**
 * Periodically sweeps and cleans up expired or invalid sessions
 * to keep the security-service session store healthy.
 */
@Component
public class SessionSweeper {

    private static final Logger logger = LoggerFactory.getLogger(SessionSweeper.class);

    private final AdminSessionService adminSessionService;
    private final CompanySessionService companySessionService;
    private final CustomerSessionService customerSessionService;

    public SessionSweeper(
            AdminSessionService adminSessionService,
            CompanySessionService companySessionService,
            CustomerSessionService customerSessionService
    ) {
        this.adminSessionService = adminSessionService;
        this.companySessionService = companySessionService;
        this.customerSessionService = customerSessionService;
    }

    /**
     * Operation: Cleanup
     *
     * Periodically scans and removes expired or invalid admin,
     * company and customer sessions from the database in order
     * to keep the session store clean and efficient.
     *
     * <p>
     * Uses:
     * <ul>
     *   <li>AdminSessionService for admin session cleanup</li>
     *   <li>CompanySessionService for company session cleanup</li>
     *   <li>CustomerSessionService for customer session cleanup</li>
     * </ul>
     * </p>
     *
     * @return nothing; side effect is the removal of stale sessions
     */
    @Scheduled(
        fixedDelayString = AppConstants.FIXED_DELAY_STRING,
        initialDelayString = AppConstants.INITIAL_DELAY_STRING
    )
    public void sweepExpiredSessions() {
        logger.info("SessionSweeper started - cleaning expired sessions...");

        adminSessionService.cleanExpiredSessions();
        companySessionService.cleanExpiredSessions();
        customerSessionService.cleanExpiredSessions();

        logger.info("SessionSweeper completed - session cleanup finished.");
    }
}
