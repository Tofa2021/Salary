package com.example.backend.employee.infrastructure.web.dto;

import java.math.BigDecimal;

public record UpdateEmploymentContractRequest(
        BigDecimal baseSalary,
        BigDecimal hourlyRate,
        BigDecimal bonusPercent,
        Integer workHoursPerWeek
) {
}
