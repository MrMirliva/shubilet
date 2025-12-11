package com.shubilet.api_gateway.mappers;

import com.shubilet.api_gateway.dataTransferObjects.internal.CookieDTO;
import jakarta.servlet.http.HttpSession;

public class HttpSessionMapper {

    public HttpSessionMapper() {

    }

    public CookieDTO toCookieDTO(HttpSession httpSession) {
        String userId = httpSession.getAttribute("userId") ==  null ? null : httpSession.getAttribute("userId").toString();
        String userType = httpSession.getAttribute("userType") ==  null ? null : httpSession.getAttribute("userType").toString();
        String authCode = httpSession.getAttribute("authCode") ==  null ? null : httpSession.getAttribute("authCode").toString();
        return new CookieDTO(userId, userType, authCode);
    }
}

