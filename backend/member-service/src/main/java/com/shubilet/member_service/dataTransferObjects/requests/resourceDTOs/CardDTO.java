package com.shubilet.member_service.dataTransferObjects.requests.resourceDTOs;

public class CardDTO {
    private int customerId;
    private int cardId;
    private String name;
    private String surname;
    private String cardNo;
    private String expirationDate;
    private String cvv;

    public CardDTO() {

    }

    public CardDTO(int customerId, int cardId) {
        this.customerId = customerId;
        this.cardId = cardId;
    }

    public CardDTO(int customerId, String name, String surname, String cardNo, String expirationDate, String cvv) {
        this.customerId = customerId;
        this.name = name;
        this.surname = surname;
        this.cardNo = cardNo;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
