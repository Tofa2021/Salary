package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.TariffRateFacade;
import com.example.backend.payroll.api.dto.rate.CreateTariffRateCommand;
import com.example.backend.payroll.api.dto.rate.TariffRateResult;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRateAmountCommand;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRatePeriodCommand;
import com.example.backend.payroll.application.service.TariffRateService;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TariffRateFacadeImpl implements TariffRateFacade {
    private final TariffRateService service;

    @Override
    @Transactional
    public TariffRateResult create(CreateTariffRateCommand cmd) {
        return service.create(cmd);
    }

    @Override
    @Transactional(readOnly = true)
    public TariffRateResult getById(Long id) {
        return service.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<TariffRateResult> getByPosition(Long positionId, PageRequest page) {
        return service.getByPosition(positionId, page);
    }

    @Override
    @Transactional(readOnly = true)
    public TariffRateResult getActual(Long positionId, LocalDate date) {
        return service.getActual(positionId, date);
    }

    @Override
    @Transactional
    public TariffRateResult changeAmount(Long id, UpdateTariffRateAmountCommand cmd) {
        return service.changeAmount(id, cmd);
    }

    @Override
    @Transactional
    public TariffRateResult changePeriod(Long id, UpdateTariffRatePeriodCommand cmd) {
        return service.changePeriod(id, cmd);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        service.delete(id);
    }
}
