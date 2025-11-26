package com.shubilet.security_service.common.util;

import java.security.SecureRandom;

import com.shubilet.security_service.common.constants.AppConstants;

public class SessionKeyGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String ALPHABET = AppConstants.ALPHABET;

    // Raw key length (without dashes)
    private static final int RAW_LENGTH = 32;

    private SessionKeyGenerator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a session key with the following format:
     * - Total length: 39 characters (including dashes)
     * @return Generated session key
     */
    public static String generate() {
        char[] raw = new char[RAW_LENGTH];

        // Step 1: Generate validation digits (1st, 5th, 17th)
        int d1, d5, d17;

        while (true) {
            d1 = RANDOM.nextInt(9);
            d5 = RANDOM.nextInt(9);
            d17 = 15 - (d1 + d5);

            if (d17 >= 0 && d17 <= 9) {
                break; // Valid combination
            }
        }

        // Place validation digits
        raw[0]  = (char) ('0' + d1);   // position 1
        raw[4]  = (char) ('0' + d5);   // position 5
        raw[16] = (char) ('0' + d17);  // position 17

        // Step 2: Fill other positions with A-Z / 0-9
        for (int i = 0; i < RAW_LENGTH; i++) {
            if (i == 0 || i == 4 || i == 16) continue; // skip validation digits
            raw[i] = ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length()));
        }

        // Step 3: Insert '-' every 4 characters
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < RAW_LENGTH; i++) {
            formatted.append(raw[i]);
            if ((i + 1) % 4 == 0 && i != RAW_LENGTH - 1) {
                formatted.append('-');
            }
        }

        // Total length = 32 raw + 7 dash = 39
        return formatted.toString();
    }
}
