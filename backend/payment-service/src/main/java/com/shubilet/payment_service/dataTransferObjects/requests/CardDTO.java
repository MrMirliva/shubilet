package com.shubilet.payment_service.dataTransferObjects.requests;


/**

    Domain: Card Management

    Represents the data transfer object used to carry credit or debit card
    information between the client application and the payment service.
    This DTO is used when a customer provides a new card during the payment
    process. Sensitive information contained in this DTO must not be stored
    in the system unless explicitly required and permitted.

    <p>

    Technologies:
    <ul>
        <li>Spring Boot – for REST controller integration</li>
        <li>Jackson – for JSON data binding</li>
        <li>Validation frameworks – to enforce constraints on incoming fields</li>
    </ul>

    Usage:
    <ul>
        <li>Submitted from the client when entering a new card for payment.</li>
        <li>Consumed by the payment controller and service for processing.</li>
    </ul>

 */
public class CardDTO {

    private String customerId;
    private String cardHolderName;
    private String cardNumber;
    private String expirationMonth;
    private String expirationYear;
    private String cvc;

    public CardDTO() {}

    public CardDTO(String customerId,
                   String cardHolderName,
                   String cardNumber,
                   String expirationMonth,
                   String expirationYear,
                   String cvc) {
        this.customerId = customerId;
        this.cardHolderName = cardHolderName;
        this.cardNumber = cardNumber;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cvc = cvc;
    }

    public String getCustomerId() {
        return customerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
}
