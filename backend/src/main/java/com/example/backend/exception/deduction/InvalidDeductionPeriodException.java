package com.example.backend.exception.deduction;

import com.example.backend.exception.InvalidStateException;

import java.time.LocalDate;

public class InvalidDeductionPeriodException extends InvalidStateException {
    public InvalidDeductionPeriodException(LocalDate from, LocalDate to) {
        super("validTo " + to + " cannot be before validFrom " + from);
    }
}
