package com.example.backend.exception.position;

import com.example.backend.exception.InvalidStateException;

public class InvalidTariffCategoryException extends InvalidStateException {
    public InvalidTariffCategoryException(int category) {
        super("Tariff category must be between 1 and 27, got: " + category);
    }
}
