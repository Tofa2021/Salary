package com.example.backend.payroll.domain.shared.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidHoursSummaryException extends InvalidStateException {
    public InvalidHoursSummaryException(String message) {
        super(message);
    }
}
