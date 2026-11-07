package com.example.backend.exception.tariffrate;

import com.example.backend.exception.AlreadyExistsException;

import java.time.LocalDate;

public class TariffRateEffectiveFromAlreadyExistsException extends AlreadyExistsException {
    public TariffRateEffectiveFromAlreadyExistsException(LocalDate date) {
        super("Tariff rate", "effective from date", date.toString());
    }
}
