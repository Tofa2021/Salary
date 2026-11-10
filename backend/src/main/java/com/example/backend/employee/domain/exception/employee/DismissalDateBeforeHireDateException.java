package com.example.backend.employee.domain.exception.employee;

import com.example.backend.shared.exception.InvalidStateException;

import java.time.LocalDate;

public class DismissalDateBeforeHireDateException extends InvalidStateException {
    public DismissalDateBeforeHireDateException(LocalDate hireDate, LocalDate dismissalDate) {
        super("dismissalDate " + dismissalDate + " cannot be before hireDate " + hireDate);
    }
}
