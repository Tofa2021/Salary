package com.example.backend.payroll.domain.core.exception;

import com.example.backend.payroll.domain.core.model.PayrollStatus;
import com.example.backend.shared.exception.ConflictException;

public class InvalidPayrollStatusException extends ConflictException {
    public InvalidPayrollStatusException(Long id, PayrollStatus from, PayrollStatus to) {
        super("Cannot transfer status from " + from + " to " + to + " for payroll with id = " + id);
    }
}
