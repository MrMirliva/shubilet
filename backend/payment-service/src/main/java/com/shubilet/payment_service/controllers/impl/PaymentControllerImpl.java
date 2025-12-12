package com.shubilet.payment_service.controllers.impl;

import com.shubilet.payment_service.controllers.PaymentController;
import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.TicketPaymentResponseDTO;
import com.shubilet.payment_service.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentControllerImpl implements PaymentController {

    private final PaymentService paymentService;

    public PaymentControllerImpl(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    @PostMapping
    public ResponseEntity<TicketPaymentResponseDTO> makePayment(@RequestBody TicketPaymentRequestDTO dto) {
        // Servis hata fırlatırsa aşağıdaki handler yakalayacak
        TicketPaymentResponseDTO response = paymentService.processTicketPayment(dto);
        return ResponseEntity.ok(response);
    }

    // --- LOCAL EXCEPTION HANDLER ---
    // Payment servisi "Yetersiz bakiye" veya "Kart yok" hatası fırlatırsa burası yakalar.
    // Not: Dönüş tipi MessageDTO olduğu için, istemci hata durumunda JSON alacağını bilir.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDTO> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(new MessageDTO("Payment Failed: " + e.getMessage()));
    }
}