package com.shubilet.member_service.common.util;

public class StringUtils {
    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
