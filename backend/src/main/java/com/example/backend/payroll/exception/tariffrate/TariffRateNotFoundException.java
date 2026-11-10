package com.example.backend.payroll.exception.tariffrate;

import com.example.backend.shared.exception.NotFoundException;

public class TariffRateNotFoundException extends NotFoundException {
    public TariffRateNotFoundException(Long id) {
        super("TariffRate not found with id = " + id);
    }

    public TariffRateNotFoundException(String message) {
        super(message);
    }
}
