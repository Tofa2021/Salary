package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.InvalidStateException;

public class EmployeeWithoutDepartmentException extends InvalidStateException {
    public EmployeeWithoutDepartmentException(Long employeeId) {
        super("Employee " + employeeId + " has no department");
    }
}
