package com.shubilet.payment_service.dataTransferObjects.requests;

/**

    Domain: Payment

    Encapsulates the information required to initiate a ticket payment
    operation. This DTO is sent from the client when a customer clicks
    the "Make Payment" button after selecting an expedition and seat.

    <p>

    Technologies:
    <ul>
        <li>Spring Boot – REST controller layer</li>
        <li>Jackson – JSON mapping between client and server</li>
    </ul>

    Usage:
    <ul>
        <li>Submitted from the client during the "Ticket Purchase" use case.</li>
        <li>Processed by the payment service to validate and perform payment.</li>
    </ul>

 */
public class TicketPaymentRequestDTO {

    private String customerId;
    private String expeditionId;
    private String seatId;
    private String amount; //could design as integer or double

    // If using a previously saved card
    private String cardId;

    // If using a new card
    private String cardHolderName;
    private String cardNumber;
    private String expirationMonth;
    private String expirationYear;
    private String cvc;

    // Should the new card be stored for future use
    private boolean saveCard;

    public TicketPaymentRequestDTO() {}

    public TicketPaymentRequestDTO(String customerId,
                                   String expeditionId,
                                   String seatId,
                                   String amount,
                                   String cardId,
                                   String cardHolderName,
                                   String cardNumber,
                                   String expirationMonth,
                                   String expirationYear,
                                   String cvc,
                                   boolean saveCard) {
        this.customerId = customerId;
        this.expeditionId = expeditionId;
        this.seatId = seatId;
        this.amount = amount;
        this.cardId = cardId;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cvc = cvc;
        this.saveCard = saveCard;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getExpeditionId() {
        return expeditionId;
    }
    public void setExpeditionId(String expeditionId) {
        this.expeditionId = expeditionId;
    }

    public String getSeatId() {
        return seatId;
    }
    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardId() {
        return cardId;
    }
    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }
    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }
    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }

    public String getCvc() {
        return cvc;
    }
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public boolean isSaveCard() {
        return saveCard;
    }
    public void setSaveCard(boolean saveCard) {
        this.saveCard = saveCard;
    }
}
