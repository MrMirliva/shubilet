package com.shubilet.security_service.common.constants;

import java.time.Duration;
import java.time.Instant;

public final class AppConstants {
    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String APP_NAME = "ShuBilet";
    public static final String DEFAULT_LOCALE = "tr-TR";
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final Instant DEFAULT_SESSION_EXPIRATION_DURATION = Instant.now().plus(Duration.ofHours(24));
}