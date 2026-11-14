package com.example.backend.payroll.domain.statutory.port;

import com.example.backend.payroll.domain.statutory.model.StatutoryRate;

import java.time.LocalDate;
import java.util.Optional;

public interface StatutoryRateRepository {
    Optional<StatutoryRate> findActiveByCodeOnDate(String code, LocalDate date);

    StatutoryRate save(StatutoryRate rate);
}
