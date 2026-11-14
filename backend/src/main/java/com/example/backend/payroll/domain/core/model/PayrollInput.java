package com.example.backend.payroll.domain.core.model;

import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.base.model.SalesSummary;
import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.model.WorkVolumeSummary;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.shared.model.HoursSummary;
import com.example.backend.payroll.domain.shared.model.Period;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

public record PayrollInput(
        Long employeeId,
        Period period,
        HoursSummary hours,
        BigDecimal normHours,
        WorkVolumeSummary volume,
        SalesSummary sales,
        TariffRate rate,
        BonusRule bonusRule,
        StatutoryRate incomeTaxRate,
        StatutoryRate socialRate,
        List<Deduction> deductions,
        List<EmployerContribution> employerContributions,
        Clock clock
) {
}
