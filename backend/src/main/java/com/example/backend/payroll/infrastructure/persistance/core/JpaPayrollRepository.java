package com.example.backend.payroll.infrastructure.persistance.core;

import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.port.PayrollRepository;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
class JpaPayrollRepository implements PayrollRepository {
    private final SpringDataPayrollRepository springDataPayrollRepository;

    @Override
    public Optional<Payroll> findById(Long id) {
        return springDataPayrollRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Payroll save(Payroll payroll) {
        PayrollJpaEntity entity = toEntity(payroll);
        PayrollJpaEntity saved = springDataPayrollRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void delete(Payroll payroll) {
        springDataPayrollRepository.deleteById(payroll.getId());
    }

    @Override
    public Optional<Payroll> findByEmployeeIdAndPeriod(Long employeeId, Period period) {
        return springDataPayrollRepository.findByEmployeeIdAndPeriodYearAndPeriodMonth(
                employeeId, period.year(), period.month()).map(this::toDomain);
    }

    @Override
    public List<Payroll> findByPeriod(Period period) {
        return springDataPayrollRepository.findByPeriodYearAndPeriodMonth(period.year(), period.month())
                .stream().map(this::toDomain).toList();
    }

    private Payroll toDomain(PayrollJpaEntity entity) {
        return Payroll.rehydrate(
                entity.getId(), entity.getEmployeeId(),
                Period.of(entity.getPeriodYear(), entity.getPeriodMonth()),
                entity.getStatus(),
                money(entity.getBaseAmount(), entity.getBaseCurrency()),
                money(entity.getGrossAmount(), entity.getGrossCurrency()),
                money(entity.getTaxAmount(), entity.getTaxCurrency()),
                money(entity.getSocialEmployeeAmount(), entity.getSocialEmployeeCurrency()),
                entity.getDeductions().stream().map(this::toDeductionResult).toList(),
                money(entity.getNetAmount(), entity.getNetCurrency()),
                money(entity.getEmployerContributionsAmount(), entity.getEmployerContributionsCurrency()),
                money(entity.getEmployerCostAmount(), entity.getEmployerCostCurrency()),
                entity.getCalculatedAt(), entity.getApprovedAt(), entity.getApprovedBy()
        );
    }

    private PayrollJpaEntity toEntity(Payroll payroll) {
        PayrollJpaEntity entity = new PayrollJpaEntity();
        entity.setId(payroll.getId());
        entity.setEmployeeId(payroll.getEmployeeId());
        entity.setPeriodYear(payroll.getPeriod().year());
        entity.setPeriodMonth(payroll.getPeriod().month());
        entity.setStatus(payroll.getStatus());
        entity.setBaseAmount(payroll.getBase() != null ? payroll.getBase().amount() : null);
        entity.setBaseCurrency(payroll.getBase() != null ? payroll.getBase().currency().getCurrencyCode() : null);
        entity.setGrossAmount(payroll.getGross().amount());
        entity.setGrossCurrency(payroll.getGross().currency().getCurrencyCode());
        entity.setTaxAmount(payroll.getTax().amount());
        entity.setTaxCurrency(payroll.getTax().currency().getCurrencyCode());
        entity.setSocialEmployeeAmount(payroll.getSocialEmployee().amount());
        entity.setSocialEmployeeCurrency(payroll.getSocialEmployee().currency().getCurrencyCode());
        entity.setNetAmount(payroll.getNet().amount());
        entity.setNetCurrency(payroll.getNet().currency().getCurrencyCode());
        entity.setEmployerContributionsAmount(payroll.getEmployerContributions().amount());
        entity.setEmployerContributionsCurrency(payroll.getEmployerContributions().currency().getCurrencyCode());
        entity.setEmployerCostAmount(payroll.getEmployerCost().amount());
        entity.setEmployerCostCurrency(payroll.getEmployerCost().currency().getCurrencyCode());
        entity.setCalculatedAt(payroll.getCalculatedAt());
        entity.setApprovedAt(payroll.getApprovedAt());
        entity.setApprovedBy(payroll.getApprovedBy());
        entity.getDeductions().clear();
        payroll.getDeductions().forEach(d -> entity.getDeductions().add(toDeductionEntity(d, entity)));
        return entity;
    }

    private DeductionResult toDeductionResult(PayrollDeductionJpaEntity entity) {
        return new DeductionResult(entity.getCode(), entity.getDescription(),
                Money.of(entity.getAmount(), Currency.getInstance(entity.getCurrency())));
    }

    private PayrollDeductionJpaEntity toDeductionEntity(DeductionResult deductionResult, PayrollJpaEntity payroll) {
        PayrollDeductionJpaEntity entity = new PayrollDeductionJpaEntity();
        entity.setPayroll(payroll);
        entity.setCode(deductionResult.code());
        entity.setDescription(deductionResult.description());
        entity.setAmount(deductionResult.amount().amount());
        entity.setCurrency(deductionResult.amount().currency().getCurrencyCode());
        return entity;
    }

    private Money money(BigDecimal amount, String currency) {
        return amount != null && currency != null ? Money.of(amount, Currency.getInstance(currency)) : null;
    }
}
