package com.example.backend.payroll.domain.base.exception;

public class InvalidWorkNormException extends RuntimeException {
    public InvalidWorkNormException(String message) {
        super(message);
    }
}
