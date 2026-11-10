package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.InvalidStateException;

public class EmployeeDismissedException extends InvalidStateException {
    public EmployeeDismissedException(Long employeeId) {
        super("Employee " + employeeId + " is dismissed");
    }
}
