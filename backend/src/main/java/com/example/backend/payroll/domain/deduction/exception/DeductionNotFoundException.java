package com.example.backend.payroll.domain.deduction.exception;

import com.example.backend.shared.exception.NotFoundException;

public class DeductionNotFoundException extends NotFoundException {
    public DeductionNotFoundException(Long id) {
        super("Deduction", "id", String.valueOf(id));
    }
}
