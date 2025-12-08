package com.shubilet.payment_service.dataTransferObjects.requests;

import java.io.Serializable;

public class TicketPaymentRequestDTO implements Serializable {
    
    private Integer cardId;
    private String amount; // BigDecimal dönüşümü serviste yapılacak
    private Integer customerId; // EKLENDİ: Güvenlik kontrolü için şart

    public TicketPaymentRequestDTO() {}

    public TicketPaymentRequestDTO(Integer cardId, String amount, Integer customerId) {
        this.cardId = cardId;
        this.amount = amount;
        this.customerId = customerId;
    }

    public Integer getCardId() { return cardId; }
    public void setCardId(Integer cardId) { this.cardId = cardId; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
}