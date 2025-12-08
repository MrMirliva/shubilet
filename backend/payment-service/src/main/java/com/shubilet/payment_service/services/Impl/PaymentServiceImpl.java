package com.shubilet.payment_service.services.Impl;

import com.shubilet.payment_service.common.enums.PaymentStatus;
import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.TicketPaymentResponseDTO;
import com.shubilet.payment_service.models.Card;
import com.shubilet.payment_service.models.Payment;
import com.shubilet.payment_service.repositories.CardRepository;
import com.shubilet.payment_service.repositories.PaymentRepository;
import com.shubilet.payment_service.services.PaymentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional // İşlemler atomik olsun (hata olursa geri alınsın)
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CardRepository cardRepository;

    // Constructor Injection
    public PaymentServiceImpl(PaymentRepository paymentRepository, CardRepository cardRepository) {
        this.paymentRepository = paymentRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public TicketPaymentResponseDTO processTicketPayment(TicketPaymentRequestDTO requestDTO) {

        // 1. Kartı veritabanından bul
        Card card = cardRepository.findById(requestDTO.getCardId())
                .orElseThrow(() -> new RuntimeException("Card not found with ID: " + requestDTO.getCardId()));

        // 2. Kartın sahibini doğrula (Güvenlik Kontrolü)
        // Request'ten gelen müşteri ID'si ile Karttaki müşteri ID'si eşleşmeli
        if (!card.getCustomerId().equals(requestDTO.getCustomerId())) {
            throw new RuntimeException("This card does not belong to the current customer!");
        }

        // 3. Kart aktif mi?
        if (!Boolean.TRUE.equals(card.getIsActive())) {
            throw new RuntimeException("Card is not active!");
        }

        // 4. Ödeme kaydı oluştur
        Payment payment = new Payment();
        payment.setCardId(card.getId());
        
        // String amount -> BigDecimal dönüşümü
        if (requestDTO.getAmount() != null) {
            payment.setAmount(new BigDecimal(requestDTO.getAmount()));
        } else {
            payment.setAmount(BigDecimal.ZERO);
        }

        Payment saved = paymentRepository.save(payment);

        // 5. Response DTO oluştur
        TicketPaymentResponseDTO responseDTO = new TicketPaymentResponseDTO();
        responseDTO.setStatus(PaymentStatus.SUCCESS.name());
        responseDTO.setMessage("Payment completed successfully.");
        responseDTO.setPaymentId(saved.getId());
        responseDTO.setTicketId("TICKET_PENDING"); // İleride Ticket servisinden gelecek

        return responseDTO;
    }
}