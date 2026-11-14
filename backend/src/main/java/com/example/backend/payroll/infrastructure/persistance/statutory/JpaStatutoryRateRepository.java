package com.example.backend.payroll.infrastructure.persistance.statutory;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.port.StatutoryRateRepository;
import lombok.RequiredArgsConstructor;
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
