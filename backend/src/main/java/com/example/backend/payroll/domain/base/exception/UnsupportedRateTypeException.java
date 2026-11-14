package com.example.backend.payroll.domain.base.exception;

import com.example.backend.payroll.domain.shared.model.RateType;
import com.example.backend.shared.exception.InvalidStateException;

public class UnsupportedRateTypeException extends InvalidStateException {
    public UnsupportedRateTypeException(RateType type) {
        super("Rate type " + type + " is unsupported");
    }
}
