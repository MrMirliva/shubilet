package com.shubilet.payment_service.services.Impl;

import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.TicketPaymentResponseDTO;
import com.shubilet.payment_service.models.Payment;
import com.shubilet.payment_service.repositories.PaymentRepository;
import com.shubilet.payment_service.services.PaymentService;
import com.shubilet.payment_service.common.enums.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public TicketPaymentResponseDTO processTicketPayment(TicketPaymentRequestDTO requestDTO) {

        // Ödeme kaydı oluştur
        Payment payment = new Payment();
        payment.setCardId(Integer.valueOf(requestDTO.getCardId()));
        payment.setAmount(new BigDecimal(requestDTO.getAmount()));

        Payment saved = paymentRepository.save(payment);

        // Response DTO oluştur
        TicketPaymentResponseDTO dto = new TicketPaymentResponseDTO();
        dto.setStatus(PaymentStatus.SUCCESS.name());
        dto.setMessage("Payment completed successfully.");
        dto.setPaymentId(String.valueOf(saved.getId()));
        dto.setTicketId("TICKET_PENDING"); 
        // ticket servisi entegre edilince gerçek değeri gelecek

        return dto;
    }
}
