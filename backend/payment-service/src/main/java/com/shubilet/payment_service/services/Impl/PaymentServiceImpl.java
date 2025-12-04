package com.shubilet.payment_service.services.Impl;

import com.shubilet.payment_service.models.Card;
import com.shubilet.payment_service.models.Payment;
import com.shubilet.payment_service.repositories.CardRepository;
import com.shubilet.payment_service.repositories.PaymentRepository;
import com.shubilet.payment_service.services.PaymentService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, CardRepository cardRepository) {
        this.paymentRepository = paymentRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public Payment makePayment(int cardId, BigDecimal amount) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found: " + cardId));

        if (!card.getIsActive()) {
            throw new IllegalStateException("Card is not active");
        }

        Payment payment = new Payment();
        payment.setCardId(cardId);
        payment.setAmount(amount);
     //   payment.setDate(LocalDateTime.now());

        return paymentRepository.save(payment);
        
    }

    @Override
    public Payment getPayment(int paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }
}

