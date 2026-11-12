package com.example.backend.employee.api.dto;

import java.math.BigDecimal;

public record UpdateEmploymentContractCommand(
        BigDecimal baseSalary,
        BigDecimal hourlyRate,
        BigDecimal bonusPercent,
        Integer workHoursPerWeek
) {
}
