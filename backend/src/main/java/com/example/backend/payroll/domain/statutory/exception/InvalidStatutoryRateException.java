package com.example.backend.payroll.domain.statutory.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidStatutoryRateException extends InvalidStateException {
    public InvalidStatutoryRateException(String message) {
        super(message);
    }
}
