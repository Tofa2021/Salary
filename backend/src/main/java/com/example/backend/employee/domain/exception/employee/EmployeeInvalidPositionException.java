package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.InvalidStateException;

public class EmployeeInvalidPositionException extends InvalidStateException {
    public EmployeeInvalidPositionException(Long id) {
        super("Cannot find position with id = " + id);
    }
}
