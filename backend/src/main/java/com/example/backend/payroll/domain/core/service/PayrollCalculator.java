package com.example.backend.payroll.domain.core.service;

import com.example.backend.payroll.domain.accrual.model.AccrualResult;
import com.example.backend.payroll.domain.accrual.service.AccrualCalculator;
import com.example.backend.payroll.domain.base.service.BaseSalaryCalculator;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.model.PayrollInput;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.deduction.service.DeductionCalculator;
import com.example.backend.payroll.domain.employer.model.EmployerResult;
import com.example.backend.payroll.domain.employer.service.EmployerCalculator;
import com.example.backend.payroll.domain.net.service.NetCalculator;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.statutory.model.StatutoryResult;
import com.example.backend.payroll.domain.statutory.service.StatutoryCalculator;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PayrollCalculator {
    private final BaseSalaryCalculator baseCalculator;
    private final AccrualCalculator accrualCalculator;
    private final StatutoryCalculator statutoryCalculator;
    private final DeductionCalculator deductionCalculator;
    private final NetCalculator netCalculator;
    private final EmployerCalculator employerCalculator;

    public Payroll calculate(PayrollInput input) {
        Money base = baseCalculator.calculate(input.rate(), input.hours(), input.volume(), input.sales(), input.normHours());

        Money hourlyRate = input.rate().hourlyRate(input.normHours());
        AccrualResult accrual = accrualCalculator.calculate(base, hourlyRate, input.hours(), input.bonusRule());
        Money gross = base.add(accrual.total());

        StatutoryResult statutory = statutoryCalculator.calculate(gross, input.incomeTaxRate(), input.socialRate());

        List<DeductionResult> deductions = deductionCalculator.calculate(
                gross, statutory.tax(), input.deductions(), input.period().end());

        Money net = netCalculator.calculate(gross, statutory.tax(),
                statutory.socialEmployee(), deductions);

        EmployerResult employer = employerCalculator.calculate(gross, input.employerContributions(), input.period().end());

        Payroll payroll = Payroll.create(input.employeeId(), input.period());
        payroll.applyCalculation(
                base, gross, statutory.tax(), statutory.socialEmployee(),
                deductions, net, employer.totalContributions(), employer.employerCost(),
                LocalDateTime.now(input.clock())
        );
        return payroll;
    }
}
