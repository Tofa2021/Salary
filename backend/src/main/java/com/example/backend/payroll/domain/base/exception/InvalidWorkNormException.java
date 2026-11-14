package com.example.backend.payroll.domain.base.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidWorkNormException extends InvalidStateException {
    public InvalidWorkNormException(String message) {
        super(message);
    }
}
