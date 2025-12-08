package com.shubilet.payment_service.controllers;

import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardDeactivationRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardIdRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CustomerIdRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.CardSummaryDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.payment_service.services.CardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Helper Method: Validasyon hatalarını tek bir string mesajına çevirir.
     */
    private String createErrorMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }

    /**
     * Yeni kart ekleme
     * @Valid anotasyonu DTO içindeki kuralları çalıştırır.
     * BindingResult, hata varsa içinde tutar.
     */
    @PostMapping("/newcard")
    public ResponseEntity<Object> saveNewCard(@RequestBody @Valid CardDTO cardDTO, BindingResult bindingResult) {
        // 1. DTO Validasyon Kontrolü
        if (bindingResult.hasErrors()) {
            String errorMsg = createErrorMessage(bindingResult);
            return ResponseEntity.badRequest().body(new MessageDTO("Validation Error: " + errorMsg));
        }

        // 2. İş Mantığı Kontrolü (Try-Catch)
        try {
            CardSummaryDTO summary = cardService.saveNewCard(cardDTO);
            return ResponseEntity.ok(new MessageDTO("Card saved successfully. ID: " + summary.getCardId()));
        } catch (Exception e) {
            // Servisten gelen RuntimeException'ları yakalayıp JSON dönüyoruz
            return ResponseEntity.badRequest().body(new MessageDTO(e.getMessage()));
        }
    }

    /**
     * Müşteri kartlarını listeleme
     */
    @PostMapping("/customer")
    public ResponseEntity<Object> getCardsByCustomer(@RequestBody CustomerIdRequestDTO requestDTO) {
        try {
            List<CardSummaryDTO> cards = cardService.getCardsByCustomer(requestDTO.getCustomerId());
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Error fetching cards: " + e.getMessage()));
        }
    }

    /**
     * Kartı pasife çekme
     */
    @PostMapping("/deactivate")
    public ResponseEntity<Object> deactivateCard(@RequestBody CardDeactivationRequestDTO requestDTO) {
        try {
            boolean success = cardService.deactivateCard(requestDTO.getCardId(), requestDTO.getCustomerId());
            if (success) {
                return ResponseEntity.ok(new MessageDTO("Card deactivated successfully."));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageDTO("Card not found or does not belong to customer."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Error deactivating card: " + e.getMessage()));
        }
    }

    /**
     * Kart aktiflik kontrolü
     */
    @PostMapping("/check-active")
    public ResponseEntity<Object> checkCardActive(@RequestBody CardIdRequestDTO requestDTO) {
        try {
            boolean isActive = cardService.isCardActive(requestDTO.getCardId());
            return ResponseEntity.ok(isActive); // true veya false döner
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageDTO("Error checking card status: " + e.getMessage()));
        }
    }
}