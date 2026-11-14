package com.example.backend.payroll.domain.core.exception;

import com.example.backend.payroll.domain.core.model.PayrollStatus;
import com.example.backend.shared.exception.InvalidStateException;

public class InvalidPayrollStatusException extends InvalidStateException {
    public InvalidPayrollStatusException(Long id, PayrollStatus from, PayrollStatus to) {
        super("Cannot transfer status from " + from + " to " + to + " for payroll with id = " + id);
    }
}
