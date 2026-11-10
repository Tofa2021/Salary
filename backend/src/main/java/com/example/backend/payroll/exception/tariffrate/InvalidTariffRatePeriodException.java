package com.example.backend.payroll.exception.tariffrate;

import com.example.backend.shared.exception.InvalidStateException;

import java.time.LocalDate;

public class InvalidTariffRatePeriodException extends InvalidStateException {
    public InvalidTariffRatePeriodException(LocalDate from, LocalDate to) {
        super("effectiveTo " + to + " cannot be before effectiveFrom " + from);
    }
}
