package com.example.backend.payroll.domain.accrual.model;

import com.example.backend.payroll.domain.shared.model.Money;

public record AccrualLine(
        String code,
        String description,
        Money amount
) {
}
