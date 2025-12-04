package com.shubilet.payment_service.services;

import com.shubilet.payment_service.models.Payment;
import java.math.BigDecimal;

public interface PaymentService {

    Payment makePayment(int cardId, BigDecimal amount);

    Payment getPayment(int paymentId);
}

