package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRatePeriodCommand;
import com.example.backend.payroll.api.dto.statutory.CreateStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.StatutoryRateResult;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;

public interface StatutoryRateFacade {
    StatutoryRateResult create(CreateStatutoryRateCommand command);

    StatutoryRateResult getById(Long id);

    StatutoryRateResult getActual(String code, LocalDate date);

    PageResult<StatutoryRateResult> getAll(String code, PageRequest pageRequest);

    StatutoryRateResult changeRate(Long id, ChangeStatutoryRateCommand command);

    StatutoryRateResult changePeriod(Long id, ChangeStatutoryRatePeriodCommand command);

    void delete(Long id);
}
