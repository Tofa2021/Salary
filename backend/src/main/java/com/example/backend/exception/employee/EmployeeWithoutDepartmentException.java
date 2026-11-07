package com.example.backend.exception.employee;

import com.example.backend.exception.InvalidStateException;

public class EmployeeWithoutDepartmentException extends InvalidStateException {
    public EmployeeWithoutDepartmentException(Long employeeId) {
        super("Employee " + employeeId + " has no department");
    }
}
