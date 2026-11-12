package com.example.backend.department.exception;

import com.example.backend.shared.exception.ConflictException;

public class DepartmentHasEmployeesException extends ConflictException {
    public DepartmentHasEmployeesException(Long id) {
        super("Department " + id + " has employees");
    }
}
