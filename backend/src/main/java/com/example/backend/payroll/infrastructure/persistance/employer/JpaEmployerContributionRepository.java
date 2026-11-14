package com.example.backend.payroll.infrastructure.persistance.employer;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.port.EmployerContributionRepository;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<EmployerContribution> findById(Long id) {
        return springDataRepository.findById(id).map(this::toDomain);
    }

    @Override
    public PageResult<EmployerContribution> findAllByCode(String code, PageRequest pageRequest) {
        return toPageResult(springDataRepository.findAllByCode(code, toPageable(pageRequest)));
    }

    @Override
    public PageResult<EmployerContribution> findAll(PageRequest page) {
        return toPageResult(springDataRepository.findAll(toPageable(page)));
    }

    @Override
    public boolean existsOverlapping(String code, DateRange period, Long excludeId) {
        return springDataRepository.existsOverlapping(code, period.from(), period.to(), excludeId);
    }

    @Override
    public void delete(EmployerContribution contribution) {
        springDataRepository.delete(toEntity(contribution));
    }

    private Pageable toPageable(PageRequest request) {
        return org.springframework.data.domain.PageRequest.of(request.page(), request.size());
    }

    private PageResult<EmployerContribution> toPageResult(Page<EmployerContributionJpaEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
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
