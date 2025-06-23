package com.example.card.service.impl;

import com.example.card.constant.CardsConstants;
import com.example.card.dto.CardsDto;
import com.example.card.entity.Cards;
import com.example.card.exception.CardAlreadyExistsException;
import com.example.card.exception.ResourceNotFoundException;
import com.example.card.mapper.CardsMapper;
import com.example.card.repository.CardsRepository;
import com.example.card.service.CardsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.smartcardio.Card;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class CardsImpl implements CardsService {


    private CardsRepository cardsRepository;


    @Override
    public void addCard(String mobileNumber){
        Optional<Cards> optionalCards = cardsRepository.findByMobileNumber(mobileNumber);
        if(optionalCards.isPresent()){
            throw new CardAlreadyExistsException("Card already registered with given mobileNumber "+mobileNumber);
        }
        cardsRepository.save(createNewCard(mobileNumber));
    }

    public Cards createNewCard(String mobileNumber){
        Cards newCard = new Cards();
        long randomNumber = 100000000000L + new Random().nextInt(900000000);
        newCard.setMobileNumber(mobileNumber);
        newCard.setCardNumber(Long.toString(randomNumber));
        newCard.setCardType(CardsConstants.CREDIT_CARD);
        newCard.setTotalLimit(CardsConstants.NEW_CARD_LIMIT);
        newCard.setAmountUsed(0);
        newCard.setAvailableAmount(CardsConstants.NEW_CARD_LIMIT);
        return newCard;
    }

    @Override
    public CardsDto fetchCards(String mobileNumber){
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                ()-> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        return CardsMapper.toCardsDto(cards, new CardsDto());
    }

    @Override
    public boolean updateCard(CardsDto cardsDto) {
        Cards cards = cardsRepository.findByCardNumber(cardsDto.getCardNumber()).orElseThrow(
                () -> new ResourceNotFoundException("Card", "CardNumber", cardsDto.getCardNumber()));
        CardsMapper.toCards(cardsDto, cards);
        cardsRepository.save(cards);
        return  true;
    }

    @Override
    public boolean deleteCard(String mobileNumber) {
        Cards cards = cardsRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Card", "mobileNumber", mobileNumber)
        );
        cardsRepository.deleteById(cards.getId());
        return true;
    }







}
