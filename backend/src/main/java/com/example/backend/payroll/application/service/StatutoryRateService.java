package com.example.backend.payroll.application.service;

import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.ChangeStatutoryRatePeriodCommand;
import com.example.backend.payroll.api.dto.statutory.CreateStatutoryRateCommand;
import com.example.backend.payroll.api.dto.statutory.StatutoryRateResult;
import com.example.backend.payroll.domain.statutory.exception.InvalidStatutoryRateException;
import com.example.backend.payroll.domain.statutory.exception.StatutoryRateNotFoundException;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.port.StatutoryRateRepository;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class StatutoryRateService {
    private final StatutoryRateRepository statutoryRateRepository;

    public StatutoryRateResult create(CreateStatutoryRateCommand command) {
        if (statutoryRateRepository.existsOverlapping(command.code(), command.validPeriod(), null)) {
            throw new InvalidStatutoryRateException(
                    "overlapping statutory rate for code " + command.code());
        }
        StatutoryRate rate = StatutoryRate.create(
                command.code(), command.rate(), command.threshold(), command.validPeriod());
        return toResult(statutoryRateRepository.save(rate));
    }

    public StatutoryRateResult getById(Long id) {
        return toResult(findById(id));
    }

    public StatutoryRateResult getActual(String code, LocalDate date) {
        return statutoryRateRepository.findActiveByCodeOnDate(code, date)
                .map(this::toResult)
                .orElseThrow(() -> new StatutoryRateNotFoundException(code, date));
    }

    public PageResult<StatutoryRateResult> getAll(String code, PageRequest page) {
        return statutoryRateRepository.findByCode(code, page).map(this::toResult);
    }

    public StatutoryRateResult changeRate(Long id, ChangeStatutoryRateCommand command) {
        StatutoryRate rate = findById(id);
        rate.changeRate(command.rate(), command.threshold());
        return toResult(statutoryRateRepository.save(rate));
    }

    public StatutoryRateResult changePeriod(Long id, ChangeStatutoryRatePeriodCommand command) {
        StatutoryRate rate = findById(id);
        if (statutoryRateRepository.existsOverlapping(rate.getCode(), command.validPeriod(), id)) {
            throw new InvalidStatutoryRateException(
                    "overlapping statutory rate for code " + rate.getCode());
        }
        rate.changePeriod(command.validPeriod());
        return toResult(statutoryRateRepository.save(rate));
    }

    public void delete(Long id) {
        statutoryRateRepository.delete(findById(id));
    }

    private StatutoryRate findById(Long id) {
        return statutoryRateRepository.findById(id)
                .orElseThrow(() -> new StatutoryRateNotFoundException(id));
    }

    private StatutoryRateResult toResult(StatutoryRate rate) {
        return new StatutoryRateResult(
                rate.getId(),
                rate.getCode(),
                rate.getRate(),
                rate.getThreshold(),
                rate.getValidPeriod()
        );
    }
}
