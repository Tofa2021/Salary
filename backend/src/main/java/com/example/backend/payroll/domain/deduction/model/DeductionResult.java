package com.example.backend.payroll.domain.deduction.model;

import com.example.backend.payroll.domain.shared.model.Money;

public record DeductionResult(
        String code,
        String description,
        Money amount
) {
    public DeductionResult {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("code is required");
        }
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
    }
}
