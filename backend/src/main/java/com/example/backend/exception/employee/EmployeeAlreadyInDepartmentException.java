package com.example.backend.exception.employee;

import com.example.backend.exception.ConflictException;

public class EmployeeAlreadyInDepartmentException extends ConflictException {
    public EmployeeAlreadyInDepartmentException(Long employeeId) {
        super("Employee with id = " + employeeId + " already in department");
    }
}
