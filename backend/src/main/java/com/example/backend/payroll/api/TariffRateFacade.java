package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.rate.CreateTariffRateCommand;
import com.example.backend.payroll.api.dto.rate.TariffRateResult;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRateAmountCommand;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRatePeriodCommand;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;

public interface TariffRateFacade {
    TariffRateResult create(CreateTariffRateCommand cmd);

    TariffRateResult getById(Long id);

    PageResult<TariffRateResult> getByPosition(Long positionId, PageRequest page);

    TariffRateResult getActual(Long positionId, LocalDate date);

    TariffRateResult changeAmount(Long id, UpdateTariffRateAmountCommand cmd);

    TariffRateResult changePeriod(Long id, UpdateTariffRatePeriodCommand cmd);

    void delete(Long id);
}
