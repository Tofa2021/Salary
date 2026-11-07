package com.example.backend.exception.tariffrate;

import com.example.backend.exception.InvalidStateException;

import java.time.LocalDate;

public class InvalidTariffRatePeriodException extends InvalidStateException {
    public InvalidTariffRatePeriodException(LocalDate from, LocalDate to) {
        super("effectiveTo " + to + " cannot be before effectiveFrom " + from);
    }
}
