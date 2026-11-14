package com.example.backend.payroll.domain.deduction.service;

import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.deduction.policy.DeductionPolicy;
import com.example.backend.payroll.domain.shared.model.Money;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class DeductionCalculator {
    private final DeductionPolicy deductionPolicy;

    public List<DeductionResult> calculate(Money gross, Money tax, List<Deduction> deductions, LocalDate date) {
        return deductionPolicy.calculate(gross, tax, deductions, date);
    }
}
