package com.example.backend.payroll.exception.tariffrate;

import com.example.backend.shared.exception.AlreadyExistsException;

import java.time.LocalDate;

public class TariffRateEffectiveFromAlreadyExistsException extends AlreadyExistsException {
    public TariffRateEffectiveFromAlreadyExistsException(LocalDate date) {
        super("Tariff rate", "effective from date", date.toString());
    }
}
