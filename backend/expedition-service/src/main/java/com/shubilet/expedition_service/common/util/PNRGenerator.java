package com.shubilet.expedition_service.common.util;

import com.shubilet.expedition_service.common.constants.AppConstants;

public class PNRGenerator {
    
    public static String generatePNR() {
        StringBuilder pnr = new StringBuilder();
        String characters = AppConstants.ALPHABET;
        int length = 6;
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * characters.length());
            pnr.append(characters.charAt(index));
        }
        return pnr.toString();
    }
}
