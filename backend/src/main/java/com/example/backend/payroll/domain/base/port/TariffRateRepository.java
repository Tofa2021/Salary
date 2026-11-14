package com.example.backend.payroll.domain.base.port;

import com.example.backend.payroll.domain.base.model.TariffRate;

import java.time.LocalDate;
import java.util.Optional;

public interface TariffRateRepository {
    Optional<TariffRate> findActiveByPositionOnDate(Long positionId, LocalDate date);

    TariffRate save(TariffRate rate);
}
