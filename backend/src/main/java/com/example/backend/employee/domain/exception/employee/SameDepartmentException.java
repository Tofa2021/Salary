package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.InvalidStateException;

public class SameDepartmentException extends InvalidStateException {
    public SameDepartmentException(Long employeeId, Long departmentId) {
        super("Employee " + employeeId + " is already in department " + departmentId);
    }
}
