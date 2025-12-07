package com.shubilet.payment_service.controllers;

import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.TicketPaymentResponseDTO;
import com.shubilet.payment_service.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // -----------------------------
    // MAKE PAYMENT
    // POST /payment
    // -----------------------------
    @PostMapping
    public ResponseEntity<TicketPaymentResponseDTO> makePayment(
            @RequestBody TicketPaymentRequestDTO dto) {

        TicketPaymentResponseDTO response = paymentService.processTicketPayment(dto);
        return ResponseEntity.ok(response);
    }
}
