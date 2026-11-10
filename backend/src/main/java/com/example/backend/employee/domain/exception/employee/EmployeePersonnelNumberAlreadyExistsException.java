package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.AlreadyExistsException;

public class EmployeePersonnelNumberAlreadyExistsException extends AlreadyExistsException {
    public EmployeePersonnelNumberAlreadyExistsException(String personnelNumber) {
        super("Employee", "personnelNumber", personnelNumber);
    }
}
