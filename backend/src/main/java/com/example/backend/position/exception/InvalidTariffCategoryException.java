package com.example.backend.position.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidTariffCategoryException extends InvalidStateException {
    public InvalidTariffCategoryException(int category) {
        super("Tariff category must be between 1 and 27, got: " + category);
    }
}
