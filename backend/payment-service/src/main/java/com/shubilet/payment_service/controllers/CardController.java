package com.shubilet.payment_service.controllers;

import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardDeactivationRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardIdRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CustomerIdRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.CardSummaryDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.List;

public interface CardController {

    // Artık ne döneceği belli: MessageDTO
    ResponseEntity<MessageDTO> saveNewCard(CardDTO cardDTO, BindingResult bindingResult);

    // Artık ne döneceği belli: Liste halinde CardSummaryDTO
    ResponseEntity<List<CardSummaryDTO>> getCardsByCustomer(CustomerIdRequestDTO requestDTO);

    // İşlem sonucu mesajı döner
    ResponseEntity<MessageDTO> deactivateCard(CardDeactivationRequestDTO requestDTO);

    // True/False döner
    ResponseEntity<Boolean> checkCardActive(CardIdRequestDTO requestDTO);
}