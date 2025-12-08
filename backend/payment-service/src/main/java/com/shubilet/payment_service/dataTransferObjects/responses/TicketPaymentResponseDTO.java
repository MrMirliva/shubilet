package com.shubilet.payment_service.dataTransferObjects.responses;

/**

    Domain: Payment

    Represents the response returned to the client after attempting a ticket
    payment operation. Contains the outcome of the payment process, including
    payment and ticket identifiers if the operation succeeds.

    <p>

    Technologies:
    <ul>
        <li>Spring Boot – REST controller layer</li>
        <li>Jackson – JSON serialization</li>
    </ul>

    Usage:
    <ul>
        <li>Returned to the client after the payment request is processed.</li>
        <li>Contains status and messages for UI feedback.</li>
    </ul>

 */
public class TicketPaymentResponseDTO {

    // SUCCESS, FAILED, PENDING gibi değerler (enum daha sonra gelecek)
    private String status;

    private String message;
    private int paymentId; // if payment is successful
    private String ticketId;  // if ticket is created successfully

    public TicketPaymentResponseDTO() {}

    public TicketPaymentResponseDTO(String status,
                                    String message,
                                    int paymentId,
                                    String ticketId) {
        this.status = status;
        this.message = message;
        this.paymentId = paymentId;
        this.ticketId = ticketId;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public int getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getTicketId() {
        return ticketId;
    }
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
}
