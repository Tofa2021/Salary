package com.example.backend.payroll.api.dto;

import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Period;

import java.time.LocalDateTime;
import java.util.List;

public record PayrollResult(
        Long id,
        Long employeeId,
        Period period,
        String status,
        Money base,
        Money gross,
        Money tax,
        Money socialEmployee,
        List<DeductionResult> deductions,
        Money net,
        Money employerContributions,
        Money employerCost,
        LocalDateTime calculatedAt,
        LocalDateTime approvedAt,
        Long approvedBy
) {
    public static PayrollResult from(Payroll payroll) {
        return new PayrollResult(
                payroll.getId(),
                payroll.getEmployeeId(),
                payroll.getPeriod(),
                payroll.getStatus().name(),
                payroll.getBase(),
                payroll.getGross(),
                payroll.getTax(),
                payroll.getSocialEmployee(),
                payroll.getDeductions(),
                payroll.getNet(),
                payroll.getEmployerContributions(),
                payroll.getEmployerCost(),
                payroll.getCalculatedAt(),
                payroll.getApprovedAt(),
                payroll.getApprovedBy()
        );
    }
}
