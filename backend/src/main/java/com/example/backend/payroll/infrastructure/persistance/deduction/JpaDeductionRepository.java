package com.example.backend.payroll.infrastructure.persistance.deduction;

import com.example.backend.payroll.domain.deduction.model.Base;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.port.DeductionRepository;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaDeductionRepository implements DeductionRepository {
    private final SpringDataDeductionRepository springDataDeductionRepository;

    @Override
    public List<Deduction> findActiveByEmployeeOnDate(Long employeeId, LocalDate date) {
        return springDataDeductionRepository.findActiveByEmployeeOnDate(employeeId, date).stream()
                .map(this::toDomain).toList();
    }

    @Override
    public Deduction save(Deduction deduction) {
        return toDomain(springDataDeductionRepository.save(toEntity(deduction)));
    }

    @Override
    public void delete(Deduction deduction) {
        springDataDeductionRepository.delete(toEntity(deduction));
    }

    private Deduction toDomain(DeductionJpaEntity entity) {
        return Deduction.rehydrate(
                entity.getId(), entity.getEmployeeId(), entity.getType(), entity.getDescription(),
                entity.getPercentage() != null ? Percentage.of(entity.getPercentage()) : null,
                entity.getFixedAmount() != null && entity.getCurrency() != null
                        ? Money.of(entity.getFixedAmount(), Currency.getInstance(entity.getCurrency()))
                        : null,
                entity.getBase() != null ? Base.valueOf(entity.getBase()) : null,
                DateRange.of(entity.getValidFrom(), entity.getValidTo())
        );
    }

    private DeductionJpaEntity toEntity(Deduction deduction) {
        DeductionJpaEntity entity = new DeductionJpaEntity();
        entity.setId(deduction.getId());
        entity.setEmployeeId(deduction.getEmployeeId());
        entity.setType(deduction.getType());
        entity.setDescription(deduction.getDescription());
        entity.setPercentage(deduction.getPercentage() != null ? deduction.getPercentage().value() : null);
        if (deduction.getFixedAmount() != null) {
            entity.setFixedAmount(deduction.getFixedAmount().amount());
            entity.setCurrency(deduction.getFixedAmount().currency().getCurrencyCode());
        } else {
            entity.setFixedAmount(null);
            entity.setCurrency(null);
        }

        entity.setBase(deduction.getBase().name());

        entity.setValidFrom(deduction.getValidPeriod().from());
        entity.setValidTo(deduction.getValidPeriod().to());

        return entity;
    }
}
