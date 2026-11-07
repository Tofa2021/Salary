package com.example.backend.exception.notFound;

public class TariffRateNotFoundException extends NotFoundException {
    public TariffRateNotFoundException(Long id) {
        super("TariffRate not found with id = " + id);
    }

    public TariffRateNotFoundException(String message) {
        super(message);
    }
}
