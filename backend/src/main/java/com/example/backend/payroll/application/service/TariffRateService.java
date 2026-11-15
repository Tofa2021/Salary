package com.example.backend.payroll.application.service;

import com.example.backend.payroll.api.dto.rate.CreateTariffRateCommand;
import com.example.backend.payroll.api.dto.rate.TariffRateResult;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRateAmountCommand;
import com.example.backend.payroll.api.dto.rate.UpdateTariffRatePeriodCommand;
import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.base.exception.TariffRateNotFoundException;
import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.port.TariffRateRepository;
import com.example.backend.payroll.domain.base.rate.RateCalculationStrategyRegistry;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.position.api.PositionLookup;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Currency;

@RequiredArgsConstructor
public class TariffRateService {
    private final TariffRateRepository repository;
    private final RateCalculationStrategyRegistry registry;
    private final PositionLookup positionLookup;

    public TariffRateResult create(CreateTariffRateCommand command) {
        positionLookup.findById(command.positionId())
                .orElseThrow(() -> new InvalidRateException("position not found: " + command.positionId()));
        if (repository.existsOverlapping(command.positionId(), command.validPeriod(), null)) {
            throw new InvalidRateException("overlapping tariff rate for position " + command.positionId());
        }
        TariffRate rate = TariffRate.create(
                command.positionId(),
                Money.of(command.amount(), Currency.getInstance(command.currency())),
                command.type(),
                command.validPeriod(),
                registry.get(command.type())
        );
        return toResult(repository.save(rate));
    }

    public TariffRateResult getById(Long id) {
        return toResult(findById(id));
    }

    public PageResult<TariffRateResult> getByPosition(Long positionId, PageRequest page) {
        return repository.findByPositionId(positionId, page).map(this::toResult);
    }

    public TariffRateResult getActual(Long positionId, LocalDate date) {
        return repository.findActiveByPositionOnDate(positionId, date)
                .map(this::toResult)
                .orElseThrow(() -> new TariffRateNotFoundException(positionId, date));
    }

    public TariffRateResult changeAmount(Long id, UpdateTariffRateAmountCommand command) {
        TariffRate rate = findById(id);
        rate.changeAmount(Money.of(command.amount(), Currency.getInstance(command.currency())));
        return toResult(repository.save(rate));
    }

    public TariffRateResult changePeriod(Long id, UpdateTariffRatePeriodCommand command) {
        TariffRate rate = findById(id);
        if (repository.existsOverlapping(rate.getPositionId(), command.validPeriod(), id)) {
            throw new InvalidRateException("overlapping tariff rate");
        }
        rate.changePeriod(command.validPeriod());
        return toResult(repository.save(rate));
    }

    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private TariffRate findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new TariffRateNotFoundException(id));
    }

    private TariffRateResult toResult(TariffRate r) {
        return new TariffRateResult(r.getId(), r.getPositionId(), r.getAmount(),
                r.getType(), r.getValidPeriod());
    }
}
