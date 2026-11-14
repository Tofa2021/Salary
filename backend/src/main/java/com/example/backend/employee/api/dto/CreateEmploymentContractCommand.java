package com.example.backend.employee.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmploymentContractCommand(
        String contractNumber,
        LocalDate startDate,
        LocalDate endDate,
        String contractType,
        BigDecimal baseSalary,
        BigDecimal hourlyRate,
        BigDecimal bonusPercent,
        int workHoursPerWeek,
        boolean mainJob
) {
}
