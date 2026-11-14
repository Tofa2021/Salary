package com.example.backend.payroll.domain.shared.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidPeriodException extends InvalidStateException {
    public InvalidPeriodException(String message) {
        super(message);
    }
}
