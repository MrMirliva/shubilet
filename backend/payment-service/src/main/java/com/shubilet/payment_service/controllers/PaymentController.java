package com.shubilet.payment_service.controllers;

import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import org.springframework.http.ResponseEntity;

public interface PaymentController {

    /**
     * Bilet ödeme işlemini başlatır.
     * Başarılı olursa bilet bilgisini, başarısız olursa hata mesajını döner.
     */
    ResponseEntity<Object> makePayment(TicketPaymentRequestDTO dto);
}