package com.shubilet.payment_service.services.Impl;

import org.springframework.stereotype.Service;

import com.shubilet.payment_service.models.Card;
import com.shubilet.payment_service.repositories.CardRepository;
import com.shubilet.payment_service.services.CardService;


@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public Card saveCard(Card card) {
        if (card.getIsActive() == null) {
            card.setIsActive(true);
        }
        return cardRepository.save(card);
    }

    @Override
    public void deleteCard(int cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Card not found with id: " + cardId));

        card.setIsActive(false);
        cardRepository.save(card);
    }

    @Override
    public Card getCard(int cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Card not found with id: " + cardId));
    }

   /*  @Override
    public Card getCardByCardNo(String cardNo) {
        return cardRepository.findByCardNo(cardNo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Card not found with cardNo: " + cardNo));
    }*/
}
