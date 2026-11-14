package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.ConflictException;

public class EmployeeWithoutDepartmentException extends ConflictException {
    public EmployeeWithoutDepartmentException(Long employeeId) {
        super("Employee " + employeeId + " has no department");
    }
}
