package com.example.backend.payroll.infrastructure.persistance.base;

import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.port.TariffRateRepository;
import com.example.backend.payroll.domain.base.rate.RateCalculationStrategyRegistry;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
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
public class JpaTariffRateRepository implements TariffRateRepository {
    private final SpringDataTariffRateRepository springDataTariffRateRepository;
    private final RateCalculationStrategyRegistry strategyRegistry;

    @Override
    public Optional<TariffRate> findActiveByPositionOnDate(Long positionId, LocalDate date) {
        return springDataTariffRateRepository.findActiveByPositionOnDate(positionId, date).map(this::toDomain);
    }

    @Override
    public Optional<TariffRate> findById(Long id) {
        return springDataTariffRateRepository.findById(id).map(this::toDomain);
    }

    @Override
    public PageResult<TariffRate> findByPositionId(Long positionId, PageRequest page) {
        return toPageResult(springDataTariffRateRepository.findByPositionId(positionId, toPageable(page)));
    }

    @Override
    public boolean existsOverlapping(Long positionId, DateRange period, Long excludeId) {
        return springDataTariffRateRepository.existsOverlapping(positionId, period.from(), period.to(), excludeId);
    }

    @Override
    public TariffRate save(TariffRate rate) {
        return toDomain(springDataTariffRateRepository.save(toEntity(rate)));
    }

    @Override
    public void delete(TariffRate rate) {
        springDataTariffRateRepository.delete(toEntity(rate));
    }

    private Pageable toPageable(PageRequest request) {
        return org.springframework.data.domain.PageRequest.of(request.page(), request.size());
    }

    private PageResult<TariffRate> toPageResult(Page<TariffRateJpaEntity> page) {
        return new PageResult<>(
                page.getContent().stream().map(this::toDomain).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements()
        );
    }

    private TariffRate toDomain(TariffRateJpaEntity entity) {
        return TariffRate.rehydrate(
                entity.getId(),
                entity.getPositionId(),
                Money.of(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
                entity.getType(),
                DateRange.of(entity.getValidFrom(), entity.getValidTo()),
                strategyRegistry.get(entity.getType())
        );
    }

    private TariffRateJpaEntity toEntity(TariffRate tariffRate) {
        TariffRateJpaEntity entity = new TariffRateJpaEntity();
        entity.setId(tariffRate.getId());
        entity.setPositionId(tariffRate.getPositionId());
        entity.setType(tariffRate.getType());
        entity.setAmount(tariffRate.getAmount().amount());
        entity.setCurrency(tariffRate.getAmount().currency().getCurrencyCode());
        entity.setValidFrom(tariffRate.getValidPeriod().from());
        entity.setValidTo(tariffRate.getValidPeriod().to());
        return entity;
    }
}
