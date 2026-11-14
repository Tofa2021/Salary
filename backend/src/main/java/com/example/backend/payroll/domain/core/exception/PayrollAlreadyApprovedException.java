package com.example.backend.payroll.domain.core.exception;

import com.example.backend.shared.exception.ConflictException;

public class PayrollAlreadyApprovedException extends ConflictException {
    public PayrollAlreadyApprovedException(Long id) {
        super("Payroll already approved with id = " + id);
    }
}
