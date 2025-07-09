package com.example.card_service.service;

import com.example.card_service.dto.CardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CardService {
    
    CardDto createCard(CardDto cardDto);
    
    CardDto createCard(CardDto cardDto, String token);
    
    CardDto getCardById(Long id);
    
    CardDto getCardByCardNumber(String cardNumber);
    
    List<CardDto> getCardsByUserId(Long userId);
    
    Page<CardDto> getAllCards(Pageable pageable);
    
    CardDto updateCard(Long id, CardDto cardDto);
    
    void deleteCard(Long id);
    
    CardDto updateCardStatus(Long id, String status);
    
    CardDto updateBalance(Long id, BigDecimal amount, String operation);
    
    CardDto blockCard(Long id);
    
    CardDto unblockCard(Long id);
    
    boolean isCardExpired(Long id);
    
    CardDto increaseLimit(Long id, BigDecimal amount);
    
    CardDto decreaseLimit(Long id, BigDecimal amount);
}
