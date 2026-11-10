package com.example.backend.shared.exception;

public abstract class InvalidStateException extends ApplicationException {
    public InvalidStateException(String message) {
        super(message);
    }
}
