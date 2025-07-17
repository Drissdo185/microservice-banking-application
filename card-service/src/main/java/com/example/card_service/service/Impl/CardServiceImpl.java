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
    @Transactional
    public CardDto createCard(CardDto cardDto, String token) {
        try {
            log.info("=== CARD CREATION STARTED ===");
            log.debug("Creating card for token: {}", token.substring(0, Math.min(10, token.length())));

            // Validate user and get user information
            UserValidationResponse userValidation = userServiceClient
                    .validateUser(token)
                    .block();

            if (userValidation == null) {
                log.error("User validation returned null");
                throw new RuntimeException("User validation failed - no response from user service");
            }

            if (!userValidation.isActive()) {
                log.error("User is not active: {}", userValidation.getId());
                throw new RuntimeException("User account is not active");
            }

            log.debug("User validation successful for userId: {}", userValidation.getId());

            // PREVENT DUPLICATE CREATION - Check for recent cards created by this user
            List<Card> recentCards = cardRepository.findByUserIdAndCreatedAtAfter(
                    userValidation.getId(),
                    LocalDateTime.now().minusMinutes(2) // Check last 2 minutes
            );

            if (!recentCards.isEmpty()) {
                log.warn("Preventing duplicate card creation for user: {}. Found {} recent cards",
                        userValidation.getId(), recentCards.size());
                // Return the most recent card instead of creating a new one
                Card mostRecentCard = recentCards.stream()
                        .max((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()))
                        .orElse(recentCards.get(0));
                return mapToDto(mostRecentCard);
            }

            // Check card limits per user
            List<Card> existingCards = cardRepository.findByUserId(userValidation.getId());
            if (existingCards.size() >= 10) { // Limit to 10 cards per user
                throw new RuntimeException("User already has maximum number of cards (10)");
            }

            // Generate unique card number (16 digits)
            String cardNum = generateUniqueCardNumber();

            // Create card entity
            Card card = mapToEntity(cardDto);
            card.setUserId(userValidation.getId());
            card.setCardNumber(cardNum);
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
            log.info("=== CARD CREATION COMPLETED - ID: {} for user: {} ===",
                    savedCard.getId(), savedCard.getUserId());
            return mapToDto(savedCard);

        } catch (Exception e) {
            log.error("=== CARD CREATION FAILED ===", e);
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
        card.setUserId(cardDto.getUserId());
        card.setCardNumber(cardDto.getCardNumber());
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
        cardDto.setId(card.getId());
        cardDto.setUserId(card.getUserId());
        cardDto.setCardNumber(card.getCardNumber());
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

    // FIXED: Generate proper 16-digit card number
    private String generateRandomCardNumber() {
        StringBuilder cardNumber = new StringBuilder();

        // Generate 16 digits for a proper credit card number
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }

    private String generateUniqueCardNumber() {
        String cardNumber;
        int attempts = 0;
        do {
            cardNumber = generateRandomCardNumber();
            attempts++;
            if (attempts > 100) {
                throw new RuntimeException("Unable to generate unique card number after 100 attempts");
            }
        } while (cardRepository.existsByCardNumber(cardNumber));

        return cardNumber;
    }
}