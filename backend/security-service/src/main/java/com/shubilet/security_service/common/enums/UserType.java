package com.shubilet.security_service.common.enums;

public enum UserType {

    ADMIN("ADMIN"),
    COMPANY("COMPANY"),
    CUSTOMER("CUSTOMER");

    private final String code;

    UserType(String code) {
        this.code = code;
    }

    /**
     * String code representation that can be stored in DB or session.
     */
    public String getCode() {
        return code;
    }

    /**
     * Safely convert from string code to enum.
     */
    public static UserType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null; // or throw IllegalArgumentException if you prefer
    }
}
