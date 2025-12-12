package com.shubilet.payment_service.controllers.impl;

import com.shubilet.payment_service.controllers.CardController;
import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardDeactivationRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardIdRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CustomerIdRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.CardSummaryDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.MessageDTO;
import com.shubilet.payment_service.services.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cards")
public class CardControllerImpl implements CardController {

    private final CardService cardService;

    public CardControllerImpl(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    @PostMapping("/newcard")
    public ResponseEntity<MessageDTO> saveNewCard(@RequestBody @Valid CardDTO cardDTO, BindingResult bindingResult) {
        // Validasyon Hatası Kontrolü
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            throw new RuntimeException("Validation Error: " + errorMsg);
        }

        CardSummaryDTO summary = cardService.saveNewCard(cardDTO);
        return ResponseEntity.ok(new MessageDTO("Card saved successfully. ID: " + summary.getCardId()));
    }

    @Override
    @PostMapping("/customer")
    public ResponseEntity<List<CardSummaryDTO>> getCardsByCustomer(@RequestBody CustomerIdRequestDTO requestDTO) {
        // Try-Catch yok, hata olursa @ExceptionHandler yakalayacak
        List<CardSummaryDTO> cards = cardService.getCardsByCustomer(requestDTO.getCustomerId());
        return ResponseEntity.ok(cards);
    }

    @Override
    @PostMapping("/deactivate")
    public ResponseEntity<MessageDTO> deactivateCard(@RequestBody CardDeactivationRequestDTO requestDTO) {
        boolean success = cardService.deactivateCard(requestDTO.getCardId(), requestDTO.getCustomerId());
        
        if (success) {
            return ResponseEntity.ok(new MessageDTO("Card deactivated successfully."));
        } else {
            // RuntimeException fırlatıyoruz ki handler yakalasın
            throw new RuntimeException("Card not found or does not belong to customer.");
        }
    }

    @Override
    @PostMapping("/check-active")
    public ResponseEntity<Boolean> checkCardActive(@RequestBody CardIdRequestDTO requestDTO) {
        boolean isActive = cardService.isCardActive(requestDTO.getCardId());
        return ResponseEntity.ok(isActive);
    }

    // --- LOCAL EXCEPTION HANDLER ---
    // Bu controller içindeki herhangi bir metot RuntimeException fırlatırsa bu metot çalışır.
    // Böylece ana metotların dönüş tipi bozulmaz (List, Boolean vb. kalabilir).
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageDTO> handleException(RuntimeException e) {
        return ResponseEntity.badRequest().body(new MessageDTO(e.getMessage()));
    }
}