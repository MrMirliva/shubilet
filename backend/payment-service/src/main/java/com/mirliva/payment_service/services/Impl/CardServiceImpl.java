package com.mirliva.payment_service.services.Impl;

import org.springframework.stereotype.Service;

import com.mirliva.payment_service.repositories.CardRepository;
import com.mirliva.payment_service.services.CardService;

///TODO: CARD SERVICE INTERFACE IMPLEMENTATION
@Service
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
}
