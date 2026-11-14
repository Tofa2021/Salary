package com.example.backend.payroll.domain.shared.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidPercentageException extends InvalidStateException {
    public InvalidPercentageException(String message) {
        super(message);
    }
}
