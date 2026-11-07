package com.example.backend.exception.employee;

import com.example.backend.exception.AlreadyExistsException;

public class EmployeePersonnelNumberAlreadyExistsException extends AlreadyExistsException {
    public EmployeePersonnelNumberAlreadyExistsException(String personnelNumber) {
        super("Employee", "personnelNumber", personnelNumber);
    }
}
