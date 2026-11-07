package com.example.backend.exception.tariffrate;

import com.example.backend.exception.NotFoundException;

public class TariffRateNotFoundException extends NotFoundException {
    public TariffRateNotFoundException(Long id) {
        super("TariffRate not found with id = " + id);
    }

    public TariffRateNotFoundException(String message) {
        super(message);
    }
}
