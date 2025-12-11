package com.shubilet.payment_service.controllers.impl;

import com.shubilet.payment_service.controllers.PaymentController;
import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.TicketPaymentResponseDTO;
import com.shubilet.payment_service.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentControllerImpl  implements PaymentController {

    private final PaymentService paymentService;

    public PaymentControllerImpl(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // -----------------------------
    // MAKE PAYMENT
    // Use Case: Main Flow 4, 5, 6
    // POST /payment
    // -----------------------------
    @PostMapping("/make")
    public ResponseEntity<Object> makePayment(@RequestBody TicketPaymentRequestDTO dto) {
        try {
            // Servise git, ödeme işlemini başlat
            TicketPaymentResponseDTO response = paymentService.processTicketPayment(dto);
            
            // Başarılı olursa (Use Case: Step 7, 8)
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Bir hata olursa (Use Case: Alternate Flow 5a - Payment verification fails)
            // Hata sebebini (Kart yok, yetki yok, format hatalı vb.) temiz bir mesajla dön.
            return ResponseEntity.badRequest().body(new MessageDTO("Payment Failed: " + e.getMessage()));
        }
    }
}