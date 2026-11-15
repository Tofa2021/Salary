package com.example.backend.payroll.domain.employer.port;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EmployerContributionRepository {
    List<EmployerContribution> findActiveOnDate(LocalDate date);

    Optional<EmployerContribution> findById(Long id);

    PageResult<EmployerContribution> findAllByCode(String code, PageRequest pageRequest);

    PageResult<EmployerContribution> findAll(PageRequest page);

    boolean existsOverlapping(String code, DateRange period, Long excludeId);

    EmployerContribution save(EmployerContribution contribution);

    void delete(EmployerContribution contribution);
}
