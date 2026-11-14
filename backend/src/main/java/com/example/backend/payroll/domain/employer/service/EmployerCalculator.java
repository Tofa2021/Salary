package com.example.backend.payroll.domain.employer.service;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.model.EmployerResult;
import com.example.backend.payroll.domain.employer.policy.EmployerContributionPolicy;
import com.example.backend.payroll.domain.shared.model.Money;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class EmployerCalculator {
    private final EmployerContributionPolicy policy;

    public EmployerResult calculate(Money gross, List<EmployerContribution> contributions, LocalDate date) {
        return policy.calculate(gross, contributions, date);
    }
}
