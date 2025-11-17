package com.mirliva.payment_service.services.Impl;

import org.springframework.stereotype.Service;

import com.mirliva.payment_service.repositories.PaymentRepository;
import com.mirliva.payment_service.services.PaymentService;

///TODO: PAYMENT SERVICE INTERFACE IMPLEMENTATION
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
}
