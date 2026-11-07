package com.example.backend.dto.employmentcontract;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateEmploymentContractRequest(
        BigDecimal baseSalary,
        BigDecimal hourlyRate,
        BigDecimal bonusPercent,
        Integer workHoursPerWeek,
        LocalDate endDate
) {
}
