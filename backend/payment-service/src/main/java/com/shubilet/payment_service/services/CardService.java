package com.shubilet.payment_service.services;

import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.CardSummaryDTO;

import java.util.List;

public interface CardService {

    List<CardSummaryDTO> getSavedCardsByCustomerId(Integer customerId);

    CardSummaryDTO saveNewCard(CardDTO cardDTO);

    void deactivateCard(Integer cardId, Integer customerId);
}
