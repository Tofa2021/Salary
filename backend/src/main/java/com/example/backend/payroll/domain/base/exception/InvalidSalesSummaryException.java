package com.example.backend.payroll.domain.base.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidSalesSummaryException extends InvalidStateException {
    public InvalidSalesSummaryException(String message) {
        super(message);
    }
}
