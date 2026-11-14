package com.example.backend.payroll.domain.deduction.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidDeductionException extends InvalidStateException {
    public InvalidDeductionException(String message) {
        super(message);
    }
}
