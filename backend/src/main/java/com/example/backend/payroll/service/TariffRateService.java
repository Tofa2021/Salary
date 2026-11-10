package com.example.backend.payroll.service;

import com.example.backend.shared.dto.tariffRate.CreateTariffRateRequest;
import com.example.backend.shared.dto.tariffRate.TariffRateEffectiveResult;
import com.example.backend.shared.dto.tariffRate.TariffRateResult;
import com.example.backend.shared.dto.tariffRate.UpdateTariffRateRequest;
import com.example.backend.payroll.exception.tariffrate.TariffRateEffectiveFromAlreadyExistsException;
import com.example.backend.payroll.exception.tariffrate.TariffRateNotFoundException;
import com.example.backend.payroll.exception.tariffrate.TariffRatePeriodOverlapException;
import com.example.backend.payroll.model.TariffRate;
import com.example.backend.payroll.repository.TariffRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TariffRateService {
    private final TariffRateRepository tariffRateRepository;

    @Transactional
    public TariffRateResult create(CreateTariffRateRequest request) {
        if (tariffRateRepository.existsByEffectiveFrom(request.effectiveFrom())) {
            throw new TariffRateEffectiveFromAlreadyExistsException(request.effectiveFrom());
        }

        tariffRateRepository.findFirstByEffectiveToIsNullOrderByEffectiveFromDesc()
                .ifPresent(previous -> {
                    LocalDate newEnd = request.effectiveFrom().minusDays(1);
                    if (previous.getEffectiveFrom().isAfter(newEnd)) {
                        throw new TariffRatePeriodOverlapException(request.effectiveFrom(), previous.getEffectiveFrom());
                    }
                    previous.setEffectiveTo(newEnd);
                });

        TariffRate rate = new TariffRate(request.baseRate(), request.effectiveFrom(), null, request.legalAct());
        return TariffRateResult.fromTariffRate(tariffRateRepository.save(rate), LocalDate.now());
    }

    @Transactional
    public TariffRateResult update(Long id, UpdateTariffRateRequest request) {
        TariffRate rate = findById(id);
        if (request.legalAct() != null) {
            rate.setLegalAct(request.legalAct());
        }
        return TariffRateResult.fromTariffRate(rate, LocalDate.now());
    }

    @Transactional
    public void delete(Long id) {
        TariffRate rate = findById(id);
        tariffRateRepository.delete(rate);
    }

    @Transactional(readOnly = true)
    public TariffRateResult getById(Long id) {
        return TariffRateResult.fromTariffRate(findById(id), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Page<TariffRateResult> getAll(Pageable pageable) {
        LocalDate today = LocalDate.now();
        return tariffRateRepository.findAll(pageable)
                .map(rate -> TariffRateResult.fromTariffRate(rate, today));
    }

    @Transactional(readOnly = true)
    public TariffRateEffectiveResult getEffective(LocalDate date) {
        LocalDate on = date != null ? date : LocalDate.now();
        TariffRate rate = tariffRateRepository.findEffectiveOn(on)
                .orElseThrow(() -> new TariffRateNotFoundException("No tariff rate effective date " + on));
        return new TariffRateEffectiveResult(rate.getId(), on, rate.getBaseRate(), rate.getLegalAct());
    }

    @Transactional(readOnly = true)
    public BigDecimal getBaseRateOn(LocalDate date) {
        return getEffective(date).baseRate();
    }

    @Transactional(readOnly = true)
    public TariffRate getEntityOn(LocalDate date) {
        return tariffRateRepository.findEffectiveOn(date)
                .orElseThrow(() -> new TariffRateNotFoundException("No tariff rate effective date " + date));
    }

    private TariffRate findById(Long id) {
        return tariffRateRepository.findById(id)
                .orElseThrow(() -> new TariffRateNotFoundException(id));
    }
}
