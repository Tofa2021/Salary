package com.example.backend.exception.tariffrate;

import com.example.backend.exception.InvalidStateException;

import java.time.LocalDate;

public class TariffRatePeriodOverlapException extends InvalidStateException {
    public TariffRatePeriodOverlapException(LocalDate newFrom, LocalDate previousFrom) {
        super("New effectiveFrom " + newFrom + " is before previous effectiveFrom " + previousFrom);
    }
}
