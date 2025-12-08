package com.shubilet.payment_service.services.Impl;

import com.shubilet.payment_service.dataTransferObjects.requests.CardDTO;
import com.shubilet.payment_service.dataTransferObjects.responses.CardSummaryDTO;
import com.shubilet.payment_service.models.Card;
import com.shubilet.payment_service.repositories.CardRepository;
import com.shubilet.payment_service.services.CardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public CardSummaryDTO saveNewCard(CardDTO cardDTO) {

        Card card = new Card();

        // 1. Customer ID
        card.setCustomerId(cardDTO.getCustomerId());

        // 2. İsim / Soyisim Ayrıştırma (DTO'dan tek string geliyor, Entity ayrı istiyor)
        String holder = cardDTO.getCardHolderName();
        if (holder != null) {
            String normalized = holder.trim().replaceAll("\\s+", " ");
            if (!normalized.isEmpty()) {
                String[] parts = normalized.split(" ", 2); // İlk boşluktan ikiye böl
                card.setName(parts[0]);
                if (parts.length > 1) {
                    card.setSurname(parts[1]);
                } else {
                    card.setSurname("-"); // Soyisim yoksa tire atıyoruz
                }
            } else {
                // Boş geldiyse
                card.setName("-");
                card.setSurname("-");
            }
        }

        // 3. Kart Numarası (Sadece rakamlar)
        String rawNumber = cardDTO.getCardNumber() != null ? cardDTO.getCardNumber() : "";
        String digitsOnly = rawNumber.replaceAll("\\D", "");
        card.setCardNo(digitsOnly);

        // 4. SKT (MM/YY formatında birleştirme)
        String month = cardDTO.getExpirationMonth() != null ? cardDTO.getExpirationMonth().trim() : "";
        String year  = cardDTO.getExpirationYear() != null ? cardDTO.getExpirationYear().trim() : "";

        // Tek hane gelirse başına 0 ekle (örn: 5 -> 05)
        if (month.length() == 1) {
            month = "0" + month;
        }
        
        // Entity "MM/YY" formatı bekliyor
        card.setExpirationDate(month + "/" + year);

        // 5. CVC (Düzeltilen alan ismi: setCVC -> setCvc)
        String cvc = cardDTO.getCvc() != null ? cardDTO.getCvc().replaceAll("\\D", "") : "";
        card.setCvc(cvc);

        // 6. Varsayılan Aktiflik
        card.setIsActive(true);

        // --- Kaydet ---
        Card saved = cardRepository.save(card);

        // --- Response DTO Oluştur ---
        return convertToSummaryDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardSummaryDTO> getCardsByCustomer(int customerId) {
        List<Card> cards = cardRepository.findByCustomerIdAndIsActiveTrue(customerId);

        return cards.stream()
                .map(this::convertToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deactivateCard(int cardId, int customerId) {
        Optional<Card> optionalCard = cardRepository.findById(cardId);
        
        if (optionalCard.isEmpty()) {
            return false;
        }

        Card card = optionalCard.get();

        // Kart bu müşteriye mi ait?
        if (!Integer.valueOf(customerId).equals(card.getCustomerId())) {
            return false;
        }

        card.setIsActive(false);
        cardRepository.save(card);

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public CardSummaryDTO getCardById(int cardId) {
        return cardRepository.findById(cardId)
                .map(this::convertToSummaryDTO)
                .orElse(null);
    }

    // --- Helper Method: DTO Çevirici ---
    private CardSummaryDTO convertToSummaryDTO(Card card) {
        String cardNo = card.getCardNo() != null ? card.getCardNo() : "";
        String last4 = cardNo.length() >= 4
                ? cardNo.substring(cardNo.length() - 4)
                : cardNo;

        // "MM/YY" formatından ay ve yılı geri ayıklama
        String exp = card.getExpirationDate() != null ? card.getExpirationDate() : "";
        String expMonth = "";
        String expYear = "";
        
        if (exp.contains("/")) {
            String[] parts = exp.split("/", 2);
            expMonth = parts[0];
            expYear = parts.length > 1 ? parts[1] : "";
        }

        return new CardSummaryDTO(
                String.valueOf(card.getId()),
                last4,
                expMonth,
                expYear
        );
    }
    // ... Diğer kodlar yukarıda ...

    @Override
    public boolean isCardActive(int cardId) {
        // 1. Veritabanına bak, kart var mı?
        // Not: 'Optional' kütüphanesi import edilmemişse java.util.Optional eklenmeli
        Optional<Card> cardOpt = cardRepository.findById(cardId);
        
        // 2. Kart yoksa direkt false dön
        if (cardOpt.isEmpty()) {
            return false;
        }

        // 3. Kart varsa, isActive alanı true mu diye bak
        // Boolean.TRUE.equals null gelirse patlamasın diye güvenli kontroldür
        return Boolean.TRUE.equals(cardOpt.get().getIsActive());
    }

} // Sınıf bitiş parantezi
