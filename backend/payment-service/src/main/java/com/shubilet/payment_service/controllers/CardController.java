package com.shubilet.payment_service.controllers;

import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardDeactivationRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CardIdRequestDTO;
import com.shubilet.payment_service.dataTransferObjects.requests.CustomerIdRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface CardController {

    /**
     * Yeni kart ekleme işlemini tanımlar.
     * Validasyon sonuçları BindingResult ile taşınır.
     */
    ResponseEntity<Object> saveNewCard(CardDTO cardDTO, BindingResult bindingResult);

    /**
     * Bir müşteriye ait aktif kartları listeler.
     */
    ResponseEntity<Object> getCardsByCustomer(CustomerIdRequestDTO requestDTO);

    /**
     * Kartı pasife çekme (silme) işlemini tanımlar.
     */
    ResponseEntity<Object> deactivateCard(CardDeactivationRequestDTO requestDTO);

    /**
     * Kartın sistemde var ve aktif olup olmadığını kontrol eder.
     */
    ResponseEntity<Object> checkCardActive(CardIdRequestDTO requestDTO);
}