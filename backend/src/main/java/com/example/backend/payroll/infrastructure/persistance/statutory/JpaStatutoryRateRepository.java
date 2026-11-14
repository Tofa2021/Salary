package com.example.backend.payroll.infrastructure.persistance.statutory;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.port.StatutoryRateRepository;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaStatutoryRateRepository implements StatutoryRateRepository {
    private final SpringDataStatutoryRateRepository springDataStatutoryRateRepository;

    @Override
    public Optional<StatutoryRate> findActiveByCodeOnDate(String code, LocalDate date) {
        return springDataStatutoryRateRepository.findActiveByCodeOnDate(code, date).map(this::toDomain);
    }

    @Override
    public StatutoryRate save(StatutoryRate rate) {
        return toDomain(springDataStatutoryRateRepository.save(toEntity(rate)));
    }

    @Override
    public boolean existsOverlapping(String code, DateRange period, Long excludeId) {
        return springDataStatutoryRateRepository.existsOverlapping(code, period.from(), period.to(), excludeId);
    }

    @Override
    public PageResult<StatutoryRate> findByCode(String code, PageRequest page) {
        return toPageResult(springDataStatutoryRateRepository.findByCode(code, toPageable(page)));
    }

    @Override
    public Optional<StatutoryRate> findById(Long id) {
        return springDataStatutoryRateRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void delete(StatutoryRate rate) {
        springDataStatutoryRateRepository.delete(toEntity(rate));
    }

    private Pageable toPageable(PageRequest request) {
        return org.springframework.data.domain.PageRequest.of(request.page(), request.size());
    }

    private PageResult<StatutoryRate> toPageResult(Page<StatutoryRateJpaEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    private StatutoryRate toDomain(StatutoryRateJpaEntity entity) {
        return StatutoryRate.rehydrate(
                entity.getId(), entity.getCode(), Percentage.of(entity.getRate()),
                entity.getThreshold() != null && entity.getThresholdCurrency() != null
                        ? Money.of(entity.getThreshold(), Currency.getInstance(entity.getThresholdCurrency()))
                        : null,
                DateRange.of(entity.getValidFrom(), entity.getValidTo())
        );
    }

    private StatutoryRateJpaEntity toEntity(StatutoryRate statutoryRate) {
        StatutoryRateJpaEntity entity = new StatutoryRateJpaEntity();
        entity.setId(statutoryRate.getId());
        entity.setCode(statutoryRate.getCode());
        entity.setRate(statutoryRate.getRate().value());
        if (statutoryRate.getThreshold() != null) {
            entity.setThreshold(statutoryRate.getThreshold().amount());
            entity.setThresholdCurrency(statutoryRate.getThreshold().currency().getCurrencyCode());
        }
        entity.setValidFrom(statutoryRate.getValidPeriod().from());
        entity.setValidTo(statutoryRate.getValidPeriod().to());
        return entity;
    }
}
