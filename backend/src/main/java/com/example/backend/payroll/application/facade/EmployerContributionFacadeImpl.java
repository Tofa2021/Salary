package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.EmployerContributionFacade;
import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionPeriodCommand;
import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionRateCommand;
import com.example.backend.payroll.api.dto.employer.CreateEmployerContributionCommand;
import com.example.backend.payroll.api.dto.employer.EmployerContributionResult;
import com.example.backend.payroll.application.service.EmployerContributionService;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployerContributionFacadeImpl implements EmployerContributionFacade {
    private final EmployerContributionService contributionService;

    @Override
    public EmployerContributionResult create(CreateEmployerContributionCommand command) {
        return contributionService.create(command);
    }

    @Override
    public EmployerContributionResult getById(Long id) {
        return contributionService.getById(id);
    }

    @Override
    public List<EmployerContributionResult> getAllActiveOnDate(LocalDate date) {
        return contributionService.getAllActiveOnDate(date);
    }

    @Override
    public PageResult<EmployerContributionResult> getAll(PageRequest page) {
        return contributionService.getAll(page);
    }

    @Override
    public EmployerContributionResult changeRate(Long id, ChangeEmployerContributionRateCommand command) {
        return contributionService.changeRate(id, command);
    }

    @Override
    public EmployerContributionResult changePeriod(Long id, ChangeEmployerContributionPeriodCommand command) {
        return contributionService.changePeriod(id, command);
    }

    @Override
    public void delete(Long id) {
        contributionService.delete(id);
    }
}
