package com.example.backend.exception.employee;

import com.example.backend.exception.InvalidStateException;

public class EmployeeDismissedException extends InvalidStateException {
    public EmployeeDismissedException(Long employeeId) {
        super("Employee " + employeeId + " is dismissed");
    }
}
