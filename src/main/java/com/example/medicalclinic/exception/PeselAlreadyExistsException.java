package com.example.clinic.exception;

public class PeselAlreadyExistsException extends RuntimeException {
    public PeselAlreadyExistsException(String message) {
        super(message);
    }
}
