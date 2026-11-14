package com.example.backend.payroll.domain.statutory.port;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;
import java.util.Optional;

public interface StatutoryRateRepository {
    Optional<StatutoryRate> findActiveByCodeOnDate(String code, LocalDate date);

    StatutoryRate save(StatutoryRate rate);

    boolean existsOverlapping(String code, DateRange period, Long excludeId);

    PageResult<StatutoryRate> findByCode(String code, PageRequest page);

    void delete(StatutoryRate rate);

    Optional<StatutoryRate> findById(Long id);
}
