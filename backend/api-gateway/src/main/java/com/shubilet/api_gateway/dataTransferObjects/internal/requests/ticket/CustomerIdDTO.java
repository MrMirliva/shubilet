package com.shubilet.api_gateway.dataTransferObjects.internal.requests.ticket;

public class CustomerIdDTO {
    private Integer customerId;

    public CustomerIdDTO() {
    
    }

    public CustomerIdDTO(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
