package com.example.backend.payroll.exception.deduction;

import com.example.backend.shared.exception.InvalidStateException;

public class UnknownDeductionTypeException extends InvalidStateException {
    public UnknownDeductionTypeException(String value) {
        super("Unknown deductionType: " + value);
    }
}
