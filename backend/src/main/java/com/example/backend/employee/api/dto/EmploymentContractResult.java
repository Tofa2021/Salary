package com.example.backend.employee.api.dto;

import com.example.backend.employee.domain.model.ContractType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmploymentContractResult(
        Long id,
        Long employeeId,
        String contractNumber,
        LocalDate startDate,
        LocalDate endDate,
        ContractType contractType,
        BigDecimal baseSalary,
        BigDecimal hourlyRate,
        BigDecimal bonusPercent,
        int workHoursPerWeek,
        boolean mainJob,
        boolean active
) {
}
