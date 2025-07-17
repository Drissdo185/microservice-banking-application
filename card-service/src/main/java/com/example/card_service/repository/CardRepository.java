package com.example.card_service.repository;

import com.example.card_service.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    
    Optional<Card> findByCardNumber(String cardNumber);
    
    List<Card> findByUserId(Long userId);
    
    List<Card> findByCardStatus(String cardStatus);
    
    List<Card> findByCardType(String cardType);
    
    List<Card> findByUserIdAndCardStatus(Long userId, String cardStatus);
    
    List<Card> findByUserIdAndCardType(Long userId, String cardType);
    
    @Query("SELECT c FROM Card c WHERE c.userId = :userId AND c.cardStatus = 'ACTIVE'")
    List<Card> findActiveCardsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Card c WHERE c.expiryYear < :currentYear OR (c.expiryYear = :currentYear AND c.expiryMonth < :currentMonth)")
    List<Card> findExpiredCards(@Param("currentYear") Integer currentYear, @Param("currentMonth") Integer currentMonth);
    
    @Query("SELECT c FROM Card c WHERE c.availableBalance < :threshold")
    List<Card> findCardsWithLowBalance(@Param("threshold") BigDecimal threshold);
    
    @Query("SELECT c FROM Card c WHERE c.creditLimit > :limit")
    List<Card> findCardsWithHighCreditLimit(@Param("limit") BigDecimal limit);
    
    @Query("SELECT COUNT(c) FROM Card c WHERE c.userId = :userId AND c.cardStatus = 'ACTIVE'")
    Long countActiveCardsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.currentBalance) FROM Card c WHERE c.userId = :userId AND c.cardStatus = 'ACTIVE'")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(c.availableBalance) FROM Card c WHERE c.userId = :userId AND c.cardStatus = 'ACTIVE'")
    BigDecimal getTotalAvailableBalanceByUserId(@Param("userId") Long userId);
    
    boolean existsByCardNumber(String cardNumber);

    @Query("SELECT c FROM Card c WHERE c.userId = :userId AND c.createdAt > :createdAfter ORDER BY c.createdAt DESC")
    List<Card> findByUserIdAndCreatedAtAfter(@Param("userId") Long userId, @Param("createdAfter") LocalDateTime createdAfter);
}
