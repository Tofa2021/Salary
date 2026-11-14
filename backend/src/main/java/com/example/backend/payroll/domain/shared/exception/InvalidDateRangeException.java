package com.example.backend.payroll.domain.shared.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidDateRangeException extends InvalidStateException {
    public InvalidDateRangeException(String message) {
        super(message);
    }
}
