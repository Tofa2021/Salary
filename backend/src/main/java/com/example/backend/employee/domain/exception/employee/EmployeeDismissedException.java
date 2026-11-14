package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.ConflictException;

public class EmployeeDismissedException extends ConflictException {
    public EmployeeDismissedException(Long employeeId) {
        super("Employee " + employeeId + " is dismissed");
    }
}
