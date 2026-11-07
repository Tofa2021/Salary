package com.example.backend.exception.employee;

import com.example.backend.exception.InvalidStateException;

public class UnknownEmploymentTypeException extends InvalidStateException {
    public UnknownEmploymentTypeException(String value) {
        super("Unknown employmentType: " + value);
    }
}
