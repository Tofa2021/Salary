package com.example.backend.exception.notFound;

public class EmployeeNotFoundException extends NotFoundException {
    public EmployeeNotFoundException(String personnelNumber) {
        super("Employee", "personnelNumber", personnelNumber);
    }

    public EmployeeNotFoundException(Long id) {
        super("Employee", "id", String.valueOf(id));
    }
}
