package com.example.backend.payroll.exception.deduction;

import com.example.backend.shared.exception.NotFoundException;

public class DeductionNotFoundException extends NotFoundException {
    public DeductionNotFoundException(Long id) {
        super("TaxDeduction", "id", String.valueOf(id));
    }
}
