package com.example.backend.payroll.domain.employer.model;

import com.example.backend.payroll.domain.shared.model.Money;

public record EmployerResult(
        java.util.List<EmployerLine> lines,
        Money totalContributions,
        Money employerCost
) {
    public EmployerResult {
        if (lines == null) {
            lines = java.util.List.of();
        }
        if (totalContributions == null || employerCost == null) {
            throw new IllegalArgumentException("totalContributions and employerCost are required");
        }
    }
}
