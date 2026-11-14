package com.example.backend.payroll.domain.employer.model;

import com.example.backend.payroll.domain.employer.exception.InvalidEmployerContributionException;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Percentage;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class EmployerContribution {
    private Long id;
    private String code;
    private Percentage rate;
    private DateRange validPeriod;

    private EmployerContribution() {
    }

    public static EmployerContribution create(String code, Percentage rate, DateRange validPeriod) {
        if (code == null || code.isBlank()) {
            throw new InvalidEmployerContributionException("code is required");
        }
        if (rate == null) {
            throw new InvalidEmployerContributionException("rate is required");
        }
        if (validPeriod == null) {
            throw new InvalidEmployerContributionException("validPeriod is required");
        }

        EmployerContribution contribution = new EmployerContribution();
        contribution.code = code;
        contribution.rate = rate;
        contribution.validPeriod = validPeriod;
        return contribution;
    }

    public static EmployerContribution rehydrate(Long id, String code, Percentage rate, DateRange validPeriod) {
        EmployerContribution contribution = new EmployerContribution();
        contribution.id = id;
        contribution.code = code;
        contribution.rate = rate;
        contribution.validPeriod = validPeriod;
        return contribution;
    }

    public boolean appliesTo(LocalDate date) {
        return validPeriod.contains(date);
    }
}
