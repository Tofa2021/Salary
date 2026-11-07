package com.example.backend.exception;

import com.example.backend.exception.alreadyExists.AlreadyExistsException;

import java.time.LocalDate;

public class TariffRateEffectiveFromAlreadyExistsException extends AlreadyExistsException {
    public TariffRateEffectiveFromAlreadyExistsException(LocalDate date) {
        super("Tariff rate", "effective from date", date.toString());
    }
}
