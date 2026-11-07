package com.example.backend.exception.position;

import com.example.backend.exception.InvalidStateException;

import java.math.BigDecimal;

public class InvalidTariffCoefficientException extends InvalidStateException {
    public InvalidTariffCoefficientException(BigDecimal coefficient) {
        super("Tariff coefficient cannot be lower than 1, got: " + coefficient);
    }
}
