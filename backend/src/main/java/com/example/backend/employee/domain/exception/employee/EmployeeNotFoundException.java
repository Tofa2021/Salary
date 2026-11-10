package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.NotFoundException;

public class EmployeeNotFoundException extends NotFoundException {
    public EmployeeNotFoundException(String personnelNumber) {
        super("Employee", "personnelNumber", personnelNumber);
    }

    public EmployeeNotFoundException(Long id) {
        super("Employee", "id", String.valueOf(id));
    }
}
