package com.example.card_service.exception;

public class InvalidCardStatusException extends RuntimeException {
    public InvalidCardStatusException(String message) {
        super(message);
    }
    
    public InvalidCardStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}