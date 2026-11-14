package com.example.backend.payroll.infrastructure.persistance.employer;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.port.EmployerContributionRepository;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Percentage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaEmployerContributionRepository implements EmployerContributionRepository {
    private SpringDataEmployerContributionRepository springDataRepository;

    @Override
    public List<EmployerContribution> findActiveOnDate(LocalDate date) {
        return springDataRepository.findActiveOnDate(date).stream().map(this::toDomain).toList();
    }

    @Override
    public EmployerContribution save(EmployerContribution contribution) {
        return toDomain(springDataRepository.save(toEntity(contribution)));
    }

    private EmployerContribution toDomain(EmployerContributionJpaEntity entity) {
        return EmployerContribution.rehydrate(
                entity.getId(),
                entity.getCode(),
                entity.getRate() != null ? Percentage.of(entity.getRate()) : null,
                DateRange.of(entity.getValidFrom(), entity.getValidTo())
        );
    }

    private EmployerContributionJpaEntity toEntity(EmployerContribution contribution) {
        EmployerContributionJpaEntity entity = new EmployerContributionJpaEntity();
        entity.setId(contribution.getId());
        entity.setCode(contribution.getCode());
        entity.setRate(contribution.getRate() != null ? contribution.getRate().value() : null);
        entity.setValidFrom(contribution.getValidPeriod().from());
        entity.setValidTo(contribution.getValidPeriod().to());

        return entity;
    }
}
