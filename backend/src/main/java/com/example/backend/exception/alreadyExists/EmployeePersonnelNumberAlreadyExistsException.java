package com.example.backend.exception.alreadyExists;

public class EmployeePersonnelNumberAlreadyExistsException extends AlreadyExistsException {
    public EmployeePersonnelNumberAlreadyExistsException(String personnelNumber) {
        super("Employee", "personnelNumber", personnelNumber);
    }
}
