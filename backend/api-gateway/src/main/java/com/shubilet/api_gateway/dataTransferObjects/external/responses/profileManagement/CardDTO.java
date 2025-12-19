package com.shubilet.api_gateway.dataTransferObjects.external.responses.profileManagement;

public class CardDTO {
    private int cardId;
    private String cardNo;
    private String name;
    private String surname;

    public CardDTO() {

    }

    public CardDTO(
        int cardId, 
        String cardNo, 
        String name, 
        String surname
    ) {
        this.cardId = cardId;
        this.cardNo = cardNo;
        this.name = name;
        this.surname = surname;
    }

    public CardDTO(
        Integer cardId, 
        String cardNo, 
        String name, 
        String surname
    ) {
        this.cardId = cardId;
        this.cardNo = cardNo;
        this.name = name;
        this.surname = surname;
    }

    public int getCardId() {
        return cardId;
    }
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public String getCardNo() {
        return cardNo;
    }
    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
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

}
