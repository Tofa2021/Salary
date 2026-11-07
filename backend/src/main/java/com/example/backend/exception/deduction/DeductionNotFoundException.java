package com.example.backend.exception.deduction;

import com.example.backend.exception.NotFoundException;

public class DeductionNotFoundException extends NotFoundException {
    public DeductionNotFoundException(Long id) {
        super("TaxDeduction", "id", String.valueOf(id));
    }
}
