package com.shubilet.payment_service.controllers;

import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.TicketPaymentResponseDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentController {

    // Object yerine gerçek DTO tipi
    ResponseEntity<TicketPaymentResponseDTO> makePayment(TicketPaymentRequestDTO dto);
}