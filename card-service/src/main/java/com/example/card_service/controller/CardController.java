package com.example.card_service.controller;

import com.example.card_service.dto.CardDto;
import com.example.card_service.service.CardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardDto> createCard(@Valid @RequestBody CardDto cardDto, 
                                             HttpServletRequest request,
                                             @RequestHeader("Authorization") String token) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        cardDto.setUserId(userId);
        
        String bearerToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        CardDto createdCard = cardService.createCard(cardDto, bearerToken);
        return new ResponseEntity<>(createdCard, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardDto> getCardById(@PathVariable Long id) {
        CardDto card = cardService.getCardById(id);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/card-number/{cardNumber}")
    public ResponseEntity<CardDto> getCardByCardNumber(@PathVariable String cardNumber) {
        CardDto card = cardService.getCardByCardNumber(cardNumber);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/user")
    public ResponseEntity<List<CardDto>> getCardsByUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        
        List<CardDto> cards = cardService.getCardsByUserId(userId);
        return ResponseEntity.ok(cards);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardDto>> getCardsByUserIdAdmin(@PathVariable Long userId) {
        List<CardDto> cards = cardService.getCardsByUserId(userId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping
    public ResponseEntity<Page<CardDto>> getAllCards(Pageable pageable) {
        Page<CardDto> cards = cardService.getAllCards(pageable);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardDto> updateCard(@PathVariable Long id, @Valid @RequestBody CardDto cardDto) {
        CardDto updatedCard = cardService.updateCard(id, cardDto);
        return ResponseEntity.ok(updatedCard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CardDto> updateCardStatus(@PathVariable Long id, @RequestParam String status) {
        CardDto updatedCard = cardService.updateCardStatus(id, status);
        return ResponseEntity.ok(updatedCard);
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<CardDto> updateBalance(@PathVariable Long id, 
                                               @RequestParam BigDecimal amount, 
                                               @RequestParam String operation) {
        CardDto updatedCard = cardService.updateBalance(id, amount, operation);
        return ResponseEntity.ok(updatedCard);
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<CardDto> blockCard(@PathVariable Long id) {
        CardDto blockedCard = cardService.blockCard(id);
        return ResponseEntity.ok(blockedCard);
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<CardDto> unblockCard(@PathVariable Long id) {
        CardDto unblockedCard = cardService.unblockCard(id);
        return ResponseEntity.ok(unblockedCard);
    }

    @GetMapping("/{id}/expired")
    public ResponseEntity<Boolean> isCardExpired(@PathVariable Long id) {
        boolean expired = cardService.isCardExpired(id);
        return ResponseEntity.ok(expired);
    }

    @PatchMapping("/{id}/increase-limit")
    public ResponseEntity<CardDto> increaseLimit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        CardDto updatedCard = cardService.increaseLimit(id, amount);
        return ResponseEntity.ok(updatedCard);
    }

    @PatchMapping("/{id}/decrease-limit")
    public ResponseEntity<CardDto> decreaseLimit(@PathVariable Long id, @RequestParam BigDecimal amount) {
        CardDto updatedCard = cardService.decreaseLimit(id, amount);
        return ResponseEntity.ok(updatedCard);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Card Service is running");
    }
}