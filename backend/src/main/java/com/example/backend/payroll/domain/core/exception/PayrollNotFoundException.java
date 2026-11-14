package com.example.backend.payroll.domain.core.exception;

import com.example.backend.shared.exception.NotFoundException;

public class PayrollNotFoundException extends NotFoundException {
    public PayrollNotFoundException(Long id) {
        super("Payroll", "id", String.valueOf(id));
    }
}
