package com.example.card.service;

import com.example.card.dto.CardsDto;

import javax.smartcardio.Card;

public interface CardsService {

    void addCard(String mobile);

    CardsDto fetchCards(String mobile);

    boolean updateCard(CardsDto cardsDto);

    boolean deleteCard(String mobile);
}
