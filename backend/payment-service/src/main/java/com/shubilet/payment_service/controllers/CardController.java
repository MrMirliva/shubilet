package com.shubilet.payment_service.controllers;

import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.CardSummaryDTO;
import com.shubilet.payment_service.services.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // -----------------------------
    // LIST SAVED CARDS
    // GET /cards/{customerId}
    // -----------------------------
    @GetMapping("/{customerId}")
    public ResponseEntity<List<CardSummaryDTO>> getCardsByCustomerId(@PathVariable Integer customerId) {
        return ResponseEntity.ok(cardService.getSavedCardsByCustomerId(customerId));
    }

    // -----------------------------
    // SAVE NEW CARD
    // POST /cards
    // -----------------------------
    @PostMapping
    public ResponseEntity<CardSummaryDTO> saveNewCard(@RequestBody CardDTO dto) {
        return ResponseEntity.ok(cardService.saveNewCard(dto));
    }

    // -----------------------------
    // DELETE (SOFT DELETE) CARD
    // DELETE /cards/{cardId}/customer/{customerId}
    // -----------------------------
    @DeleteMapping("/{cardId}/customer/{customerId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Integer cardId,
                                           @PathVariable Integer customerId) {
        cardService.deactivateCard(cardId, customerId);
        return ResponseEntity.noContent().build();
    }
}
