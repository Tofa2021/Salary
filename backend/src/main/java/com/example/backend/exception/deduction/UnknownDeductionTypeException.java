package com.example.backend.exception.deduction;

import com.example.backend.exception.InvalidStateException;

public class UnknownDeductionTypeException extends InvalidStateException {
    public UnknownDeductionTypeException(String value) {
        super("Unknown deductionType: " + value);
    }
}
