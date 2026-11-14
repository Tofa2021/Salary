package com.example.backend.payroll.domain.accrual.model;

import com.example.backend.payroll.domain.shared.model.Money;

import java.util.List;

public record AccrualResult(
        List<AccrualLine> lines,
        Money total
) {
    public AccrualResult {
        if (lines == null) lines = List.of();
        if (total == null) throw new IllegalArgumentException("total is required");
    }

    public static AccrualResult of(List<AccrualLine> lines, Money total) {
        return new AccrualResult(lines, total);
    }
}
