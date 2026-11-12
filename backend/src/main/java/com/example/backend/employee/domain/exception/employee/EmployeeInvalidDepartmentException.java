package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.InvalidStateException;

public class EmployeeInvalidDepartmentException extends InvalidStateException {
    public EmployeeInvalidDepartmentException(Long id) {
        super("Cannot find department with id = " + id);
    }
}
