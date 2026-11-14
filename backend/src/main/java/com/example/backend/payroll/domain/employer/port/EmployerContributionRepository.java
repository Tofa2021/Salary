package com.example.backend.payroll.domain.employer.port;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;

import java.time.LocalDate;
import java.util.List;

public interface EmployerContributionRepository {
    List<EmployerContribution> findActiveOnDate(LocalDate date);

    EmployerContribution save(EmployerContribution contribution);
}
