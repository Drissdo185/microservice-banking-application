package com.example.card_service.service.Impl;

import com.example.card_service.client.UserServiceClient;
import com.example.card_service.dto.CardDto;
import com.example.card_service.dto.UserValidationResponse;
import com.example.card_service.entity.Card;
import com.example.card_service.exception.CardNotFoundException;
import com.example.card_service.exception.InsufficientBalanceException;
import com.example.card_service.exception.InvalidCardStatusException;
import com.example.card_service.repository.CardRepository;
import com.example.card_service.service.CardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardServiceImpl implements CardService {

    private final Random random = new SecureRandom();
    private final CardRepository cardRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public CardDto createCard(CardDto cardDto) {
        Card card = mapToEntity(cardDto);
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        card.setCardStatus("ACTIVE");
        
        if (card.getCurrentBalance() == null) {
            card.setCurrentBalance(BigDecimal.ZERO);
        }
        
        if (card.getAvailableBalance() == null) {
            card.setAvailableBalance(card.getCreditLimit());
        }
        
        Card savedCard = cardRepository.save(card);
        return mapToDto(savedCard);
    }

    @Override
    public CardDto createCard(CardDto cardDto, String token) {

        
        try {
            UserValidationResponse userValidation = userServiceClient
                    .validateUser(token)
                    .block();
            
            if (userValidation == null || !userValidation.isActive()) {
                throw new RuntimeException("User validation failed");
            }

            String cardNum = generateRandomCardNumber();

            
            Card card = mapToEntity(cardDto);
            card.setUserId(userValidation.getId());

            if(cardRepository.findByCardNumber(cardNum) != null) {
                throw new RuntimeException("Card number already exists");
            }else{
                card.setCardNumber(cardNum);
            }
            card.setCreatedAt(LocalDateTime.now());
            card.setUpdatedAt(LocalDateTime.now());
            card.setCardStatus("ACTIVE");
            
            if (card.getCurrentBalance() == null) {
                card.setCurrentBalance(BigDecimal.ZERO);
            }
            
            if (card.getAvailableBalance() == null) {
                card.setAvailableBalance(card.getCreditLimit());
            }
            
            Card savedCard = cardRepository.save(card);
            log.debug("Card created successfully with ID: {}", savedCard.getId());
            return mapToDto(savedCard);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to create card: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CardDto getCardById(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
        return mapToDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public CardDto getCardByCardNumber(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new CardNotFoundException("Card not found with card number: " + cardNumber));
        return mapToDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardDto> getCardsByUserId(Long userId) {
        List<Card> cards = cardRepository.findByUserId(userId);
        return cards.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CardDto> getAllCards(Pageable pageable) {
        Page<Card> cards = cardRepository.findAll(pageable);
        return cards.map(this::mapToDto);
    }

    @Override
    public CardDto updateCard(Long id, CardDto cardDto) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
        
        existingCard.setCardHolderName(cardDto.getCardHolderName());
        existingCard.setExpiryMonth(cardDto.getExpiryMonth());
        existingCard.setExpiryYear(cardDto.getExpiryYear());
        existingCard.setCardType(cardDto.getCardType());
        existingCard.setCreditLimit(cardDto.getCreditLimit());
        existingCard.setUpdatedAt(LocalDateTime.now());
        
        Card updatedCard = cardRepository.save(existingCard);
        return mapToDto(updatedCard);
    }

    @Override
    public void deleteCard(Long id) {
        if (!cardRepository.existsById(id)) {
            throw new CardNotFoundException("Card not found with id: " + id);
        }
        cardRepository.deleteById(id);
    }

    @Override
    public CardDto updateCardStatus(Long id, String status) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
        
        if (!status.matches("^(ACTIVE|BLOCKED|EXPIRED)$")) {
            throw new InvalidCardStatusException("Invalid card status: " + status);
        }
        
        card.setCardStatus(status);
        card.setUpdatedAt(LocalDateTime.now());
        
        Card updatedCard = cardRepository.save(card);
        return mapToDto(updatedCard);
    }

    @Override
    public CardDto updateBalance(Long id, BigDecimal amount, String operation) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
        
        if (!"ACTIVE".equals(card.getCardStatus())) {
            throw new InvalidCardStatusException("Cannot update balance for inactive card");
        }
        
        BigDecimal newBalance;
        if ("DEBIT".equalsIgnoreCase(operation)) {
            if (card.getAvailableBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient available balance");
            }
            newBalance = card.getCurrentBalance().add(amount);
            card.setAvailableBalance(card.getAvailableBalance().subtract(amount));
        } else if ("CREDIT".equalsIgnoreCase(operation)) {
            newBalance = card.getCurrentBalance().subtract(amount);
            card.setAvailableBalance(card.getAvailableBalance().add(amount));
            
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                newBalance = BigDecimal.ZERO;
            }
        } else {
            throw new IllegalArgumentException("Invalid operation: " + operation);
        }
        
        card.setCurrentBalance(newBalance);
        card.setUpdatedAt(LocalDateTime.now());
        
        Card updatedCard = cardRepository.save(card);
        return mapToDto(updatedCard);
    }

    @Override
    public CardDto blockCard(Long id) {
        return updateCardStatus(id, "BLOCKED");
    }

    @Override
    public CardDto unblockCard(Long id) {
        return updateCardStatus(id, "ACTIVE");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCardExpired(Long id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
        
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() > card.getExpiryYear() || 
               (now.getYear() == card.getExpiryYear() && now.getMonthValue() > card.getExpiryMonth());
    }

    @Override
    public CardDto increaseLimit(Long id, BigDecimal amount) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        BigDecimal newLimit = card.getCreditLimit().add(amount);
        BigDecimal balanceIncrease = amount;
        
        card.setCreditLimit(newLimit);
        card.setAvailableBalance(card.getAvailableBalance().add(balanceIncrease));
        card.setUpdatedAt(LocalDateTime.now());
        
        Card updatedCard = cardRepository.save(card);
        return mapToDto(updatedCard);
    }

    @Override
    public CardDto decreaseLimit(Long id, BigDecimal amount) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        BigDecimal newLimit = card.getCreditLimit().subtract(amount);
        
        if (newLimit.compareTo(card.getCurrentBalance()) < 0) {
            throw new IllegalArgumentException("Cannot decrease limit below current balance");
        }
        
        BigDecimal balanceDecrease = amount;
        
        card.setCreditLimit(newLimit);
        card.setAvailableBalance(card.getAvailableBalance().subtract(balanceDecrease));
        card.setUpdatedAt(LocalDateTime.now());
        
        Card updatedCard = cardRepository.save(card);
        return mapToDto(updatedCard);
    }

    private Card mapToEntity(CardDto cardDto) {
        Card card = new Card();
        card.setCardHolderName(cardDto.getCardHolderName());
        card.setExpiryMonth(cardDto.getExpiryMonth());
        card.setExpiryYear(cardDto.getExpiryYear());
        card.setCardType(cardDto.getCardType());
        card.setCardStatus(cardDto.getCardStatus());
        card.setCreditLimit(cardDto.getCreditLimit());
        card.setCurrentBalance(cardDto.getCurrentBalance());
        card.setAvailableBalance(cardDto.getAvailableBalance());
        card.setCreatedAt(cardDto.getCreatedAt());
        card.setUpdatedAt(cardDto.getUpdatedAt());
        return card;
    }

    private CardDto mapToDto(Card card) {
        CardDto cardDto = new CardDto();
        cardDto.setCardHolderName(card.getCardHolderName());
        cardDto.setExpiryMonth(card.getExpiryMonth());
        cardDto.setExpiryYear(card.getExpiryYear());
        cardDto.setCardType(card.getCardType());
        cardDto.setCardStatus(card.getCardStatus());
        cardDto.setCreditLimit(card.getCreditLimit());
        cardDto.setCurrentBalance(card.getCurrentBalance());
        cardDto.setAvailableBalance(card.getAvailableBalance());
        cardDto.setCreatedAt(card.getCreatedAt());
        cardDto.setUpdatedAt(card.getUpdatedAt());
        return cardDto;
    }

    private String generateRandomCardNumber() {
        StringBuilder cardNumber = new StringBuilder();


        for (int i = 1; i < 10; i++) {
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }

}
