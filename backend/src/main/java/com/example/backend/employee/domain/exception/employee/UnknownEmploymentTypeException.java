package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.InvalidStateException;

public class UnknownEmploymentTypeException extends InvalidStateException {
    public UnknownEmploymentTypeException(String value) {
        super("Unknown employmentType: " + value);
    }
}
