package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.ConflictException;

public class SameDepartmentException extends ConflictException {
    public SameDepartmentException(Long employeeId, Long departmentId) {
        super("Employee " + employeeId + " is already in department " + departmentId);
    }
}
