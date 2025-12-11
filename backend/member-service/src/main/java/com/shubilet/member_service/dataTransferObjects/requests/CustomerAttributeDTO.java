package com.shubilet.member_service.dataTransferObjects.requests;

public class CustomerAttributeDTO {
    private int customerId;
    private String attribute;

    public CustomerAttributeDTO() {

    }

    public CustomerAttributeDTO(int customerId, String attribute) {
        this.customerId = customerId;
        this.attribute = attribute;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
