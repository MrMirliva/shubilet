package com.shubilet.payment_service.services;

import com.shubilet.payment_service.dataTransferObjects.requests.TicketPaymentRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.TicketPaymentResponseDTO;

public interface PaymentService {

    TicketPaymentResponseDTO processTicketPayment(TicketPaymentRequestDTO requestDTO);
}