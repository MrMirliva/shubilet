package com.shubilet.api_gateway.dataTransferObjects.external.requests;

public class CustomerAttributeDTO {
    private int customerId;
    private String attribute;

    public CustomerAttributeDTO() {

    }

    public CustomerAttributeDTO(String attribute) {
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
