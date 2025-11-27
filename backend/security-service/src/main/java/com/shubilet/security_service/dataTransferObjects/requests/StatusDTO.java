package com.shubilet.security_service.dataTransferObjects.requests;

import com.shubilet.security_service.common.enums.SessionStatus;

public class StatusDTO {
    private SessionStatus status;
    private String message = "";

    public StatusDTO(SessionStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public StatusDTO(SessionStatus status) {
        this.status = status;
    }

    public SessionStatus getStatus() {
        return status;
    }
    public void setStatus(SessionStatus status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
