package com.example.backend.payroll.domain.core.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class PayrollCalculationException extends InvalidStateException {
    public PayrollCalculationException(String message) {
        super(message);
    }
}
