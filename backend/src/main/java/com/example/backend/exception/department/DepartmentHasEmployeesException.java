package com.example.backend.exception.department;

import com.example.backend.exception.ConflictException;

public class DepartmentHasEmployeesException extends ConflictException {
    public DepartmentHasEmployeesException(Long id, long employeeCount) {
        super("Department " + id + " has " + employeeCount + " employees");
    }
}
