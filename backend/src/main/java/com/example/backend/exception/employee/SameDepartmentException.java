package com.example.backend.exception.employee;

import com.example.backend.exception.InvalidStateException;

public class SameDepartmentException extends InvalidStateException {
    public SameDepartmentException(Long employeeId, Long departmentId) {
        super("Employee " + employeeId + " is already in department " + departmentId);
    }
}
