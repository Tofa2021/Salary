package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.ConflictException;

public class EmployeeAlreadyInDepartmentException extends ConflictException {
    public EmployeeAlreadyInDepartmentException(Long employeeId) {
        super("Employee with id = " + employeeId + " already in department");
    }
}
