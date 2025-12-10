package com.shubilet.expedition_service.common.constants;

public final class ValidationPatterns {
    private ValidationPatterns() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";
    ///TODO: HH:mm şeklinde olmalı, saniye olmamalı.
    public static final String TIME_PATTERN = "^(?:[01]\\d|2[0-3]):[0-5]\\d$";
}
