package com.example.backend.payroll.domain.statutory.model;

import com.example.backend.payroll.domain.shared.model.Money;

import java.util.List;

public record StatutoryResult(
        Money tax,
        Money socialEmployee,
        List<StatutoryLine> lines
) {
    public StatutoryResult {
        if (tax == null || socialEmployee == null) {
            throw new IllegalArgumentException("tax and socialEmployee are required");
        }
        if (lines == null) {
            lines = List.of();
        }
    }

    public Money total() {
        return tax.add(socialEmployee);
    }
}
