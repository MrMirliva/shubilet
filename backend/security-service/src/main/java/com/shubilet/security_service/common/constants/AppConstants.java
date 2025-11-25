package com.shubilet.security_service.common.constants;

///TODO: Gerçekten gerekli mi kontrol et
public final class AppConstants {
    private AppConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String APP_NAME = "ShuBilet";
    public static final String DEFAULT_LOCALE = "tr-TR";
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}