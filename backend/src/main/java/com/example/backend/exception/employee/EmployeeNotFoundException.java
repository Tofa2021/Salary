package com.example.backend.exception.employee;

import com.example.backend.exception.NotFoundException;

public class EmployeeNotFoundException extends NotFoundException {
    public EmployeeNotFoundException(String personnelNumber) {
        super("Employee", "personnelNumber", personnelNumber);
    }

    public EmployeeNotFoundException(Long id) {
        super("Employee", "id", String.valueOf(id));
    }
}
