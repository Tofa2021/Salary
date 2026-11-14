package com.example.backend.payroll.infrastructure.persistance.accrual;

import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.port.BonusRuleRepository;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaBonusRuleRepository implements BonusRuleRepository {
    private final BonusRuleSpringDataRepository bonusRuleSpringDataRepository;

    @Override
    public Optional<BonusRule> findActiveByPositionOrEmployeeOnDate(
            Long positionId, Long employeeId, LocalDate date) {
        return bonusRuleSpringDataRepository.findActiveByPositionOrEmployeeOnDate(positionId, employeeId, date)
                .stream().findFirst()
                .map(this::toDomain);
    }

    @Override
    public BonusRule save(BonusRule rule) {
        return toDomain(bonusRuleSpringDataRepository.save(toEntity(rule)));
    }

    private BonusRule toDomain(BonusRuleJpaEntity entity) {
        return BonusRule.rehydrate(
                entity.getId(), entity.getPositionId(), entity.getEmployeeId(),
                entity.getType(),
                entity.getPercentage() != null ? Percentage.of(entity.getPercentage()) : null,
                entity.getFixedAmount() != null && entity.getCurrency() != null
                        ? Money.of(entity.getFixedAmount(), Currency.getInstance(entity.getCurrency()))
                        : null,
                DateRange.of(entity.getValidFrom(), entity.getValidTo())
        );
    }

    private BonusRuleJpaEntity toEntity(BonusRule rule) {
        BonusRuleJpaEntity entity = new BonusRuleJpaEntity();
        entity.setId(rule.getId());
        entity.setPositionId(rule.getPositionId());
        entity.setEmployeeId(rule.getEmployeeId());
        entity.setType(rule.getType());
        entity.setPercentage(rule.getPercentage() != null ? rule.getPercentage().value() : null);
        if (rule.getFixedAmount() != null) {
            entity.setFixedAmount(rule.getFixedAmount().amount());
            entity.setCurrency(rule.getFixedAmount().currency().getCurrencyCode());
        }
        entity.setValidFrom(rule.getValidPeriod().from());
        entity.setValidTo(rule.getValidPeriod().to());
        return entity;
    }
}
