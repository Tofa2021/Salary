package com.example.backend.payroll.application.service;

import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionPeriodCommand;
import com.example.backend.payroll.api.dto.employer.ChangeEmployerContributionRateCommand;
import com.example.backend.payroll.api.dto.employer.CreateEmployerContributionCommand;
import com.example.backend.payroll.api.dto.employer.EmployerContributionResult;
import com.example.backend.payroll.domain.employer.exception.EmployerContributionNotFoundException;
import com.example.backend.payroll.domain.employer.exception.InvalidEmployerContributionException;
import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.port.EmployerContributionRepository;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class EmployerContributionService {
    private final EmployerContributionRepository repository;

    public EmployerContributionResult create(CreateEmployerContributionCommand command) {
        if (repository.existsOverlapping(command.code(), command.validPeriod(), null)) {
            throw new InvalidEmployerContributionException("overlapping employer contribution for code " + command.code());
        }
        EmployerContribution contribution = EmployerContribution.create(command.code(), command.rate(), command.validPeriod());
        return toResult(repository.save(contribution));
    }

    public EmployerContributionResult getById(Long id) {
        return toResult(findById(id));
    }

    public List<EmployerContributionResult> getAllActiveOnDate(LocalDate date) {
        return repository.findActiveOnDate(date).stream().map(this::toResult).toList();
    }

    public PageResult<EmployerContributionResult> getAll(PageRequest request) {
        return repository.findAll(request).map(this::toResult);
    }

    public EmployerContributionResult changeRate(Long id, ChangeEmployerContributionRateCommand command) {
        EmployerContribution contribution = findById(id);
        contribution.changeRate(command.rate());
        return toResult(repository.save(contribution));
    }

    public EmployerContributionResult changePeriod(Long id, ChangeEmployerContributionPeriodCommand command) {
        EmployerContribution contribution = findById(id);
        if (repository.existsOverlapping(contribution.getCode(), command.validPeriod(), id)) {
            throw new InvalidEmployerContributionException("overlapping employer contribution for code " + contribution.getCode());
        }
        contribution.changePeriod(command.validPeriod());
        return toResult(repository.save(contribution));
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private EmployerContribution findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmployerContributionNotFoundException(id));
    }

    private EmployerContributionResult toResult(EmployerContribution contribution) {
        return new EmployerContributionResult(
                contribution.getId(),
                contribution.getCode(),
                contribution.getRate(),
                contribution.getValidPeriod()
        );
    }
}
