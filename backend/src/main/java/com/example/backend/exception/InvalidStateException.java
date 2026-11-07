package com.example.backend.exception;

public abstract class InvalidStateException extends ApplicationException {
    public InvalidStateException(String message) {
        super(message);
    }
}
