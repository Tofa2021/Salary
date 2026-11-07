package com.example.backend.dto.employmentcontract;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEmploymentContractRequest(
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
