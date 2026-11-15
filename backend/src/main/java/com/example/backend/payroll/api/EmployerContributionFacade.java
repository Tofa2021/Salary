package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionPeriodCommand;
import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionRateCommand;
import com.example.backend.payroll.api.dto.employer.CreateEmployerContributionCommand;
import com.example.backend.payroll.api.dto.employer.EmployerContributionResult;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;
import java.util.List;

public interface EmployerContributionFacade {
    EmployerContributionResult create(CreateEmployerContributionCommand cmd);

    EmployerContributionResult getById(Long id);

    List<EmployerContributionResult> getAllActiveOnDate(LocalDate date);

    PageResult<EmployerContributionResult> getAll(PageRequest page);

    EmployerContributionResult changeRate(Long id, ChangeEmployerContributionRateCommand cmd);

    EmployerContributionResult changePeriod(Long id, ChangeEmployerContributionPeriodCommand cmd);

    void delete(Long id);
}
