package com.shubilet.payment_service.services;

import com.shubilet.payment_service.models.Card;
import java.util.List;

public interface CardService {

    Card saveCard(Card card);

    void deleteCard(int cardId);

    Card getCard(int cardId);

   // Card getCardByCardNo(String cardNo);
}
