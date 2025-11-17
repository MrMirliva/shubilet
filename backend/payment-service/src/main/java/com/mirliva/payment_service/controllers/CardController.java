package com.mirliva.payment_service.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mirliva.payment_service.services.CardService;

///TODO: CARD CONTROLLER IMPLEMENTATION
@RestController
@RequestMapping("/api/cards")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class CardController {
    
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }
}
