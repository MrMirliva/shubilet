package com.shubilet.payment_service.services.Impl;

import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.CardSummaryDTO;
import com.shubilet.payment_service.models.Card;
import com.shubilet.payment_service.repositories.CardRepository;
import com.shubilet.payment_service.services.CardService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public List<CardSummaryDTO> getSavedCardsByCustomerId(Integer customerId) {

        List<Card> cards = cardRepository.findByCustomerId(customerId);
        List<CardSummaryDTO> list = new ArrayList<>();

        for (Card c : cards) {
            if (Boolean.TRUE.equals(c.getIsActive())) { // yalnızca aktif kartlar listelenecek
                CardSummaryDTO dto = new CardSummaryDTO();
                dto.setCardId(String.valueOf(c.getId()));
                dto.setLast4Digits(c.getCardNo().substring(c.getCardNo().length() - 4));
                dto.setExpirationMonth(c.getExpirationDate().substring(0, 2));
                dto.setExpirationYear(c.getExpirationDate().substring(3, 5));
                list.add(dto);
            }
        }

        return list;
    }

    @Override
    public CardSummaryDTO saveNewCard(CardDTO cardDTO) {

        Card c = new Card();
        c.setCardNo(cardDTO.getCardNumber());
        c.setExpirationDate(cardDTO.getExpirationMonth() + "/" + cardDTO.getExpirationYear());
        c.setCVC(cardDTO.getCvc());

        // CardDTO'da name + surname yoktu, cardHolderName vardı → parçalayarak set ediyoruz
        String[] parts = cardDTO.getCardHolderName().trim().split(" ", 2);
        c.setName(parts[0]);
        c.setSurname(parts.length > 1 ? parts[1] : "");

        c.setCustomerId(Integer.valueOf(cardDTO.getCustomerId()));
        c.setIsActive(true);

        Card saved = cardRepository.save(c);

        CardSummaryDTO dto = new CardSummaryDTO();
        dto.setCardId(String.valueOf(saved.getId()));
        dto.setLast4Digits(saved.getCardNo().substring(saved.getCardNo().length() - 4));
        dto.setExpirationMonth(saved.getExpirationDate().substring(0, 2));
        dto.setExpirationYear(saved.getExpirationDate().substring(3, 5));

        return dto;
    }

    @Override
    public void deactivateCard(Integer cardId, Integer customerId) {

        Card card = cardRepository.findByIdAndCustomerId(cardId, customerId)
                .orElseThrow(() -> new RuntimeException("Card not found or access denied."));

        card.setIsActive(false);
        cardRepository.save(card);
    }
}

