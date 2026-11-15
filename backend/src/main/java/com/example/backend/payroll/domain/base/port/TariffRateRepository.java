package com.example.backend.payroll.domain.base.port;

import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;
import java.util.Optional;

public interface TariffRateRepository {
    Optional<TariffRate> findActiveByPositionOnDate(Long positionId, LocalDate date);

    Optional<TariffRate> findById(Long id);

    PageResult<TariffRate> findByPositionId(Long positionId, PageRequest page);

    boolean existsOverlapping(Long positionId, DateRange period, Long excludeId);

    TariffRate save(TariffRate rate);

    void delete(TariffRate rate);
}
