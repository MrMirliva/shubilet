package com.shubilet.expedition_service.common.enums;

public enum SeatStatus {
        AVAILABLE("Available"),
        RESERVED("Reserved");

        private final String displayName;

        SeatStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
