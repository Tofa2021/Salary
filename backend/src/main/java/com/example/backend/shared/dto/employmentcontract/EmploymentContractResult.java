package com.example.backend.shared.dto.employmentcontract;

import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.domain.model.EmploymentContract;

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
    public static EmploymentContractResult from(EmploymentContract contract, LocalDate today) {
        boolean active = !contract.getStartDate().isAfter(today) &&
                (contract.getEndDate() == null || !contract.getEndDate().isBefore(today));
        return new EmploymentContractResult(
                contract.getId(),
                contract.getEmployee().getId(),
                contract.getContractNumber(),
                contract.getStartDate(),
                contract.getEndDate(),
                contract.getContractType(),
                contract.getBaseSalary(),
                contract.getHourlyRate(),
                contract.getBonusPercent(),
                contract.getWorkHoursPerWeek(),
                contract.isMainJob(),
                active
        );
    }
}
