package com.example.backend.payroll.domain.base.model;

import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.base.rate.RateCalculationContext;
import com.example.backend.payroll.domain.base.rate.RateCalculationStrategy;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.RateType;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class TariffRate {
    private Long id;
    private Long positionId;
    private Money amount;
    private RateType type;
    private DateRange validPeriod;
    private RateCalculationStrategy strategy;

    private TariffRate() {
    }

    public static TariffRate create(
            Long positionId,
            Money amount,
            RateType type,
            DateRange validPeriod,
            RateCalculationStrategy strategy
    ) {
        if (positionId == null) {
            throw new InvalidRateException("positionId is required");
        }
        if (amount == null) {
            throw new InvalidRateException("amount is required");
        }
        if (amount.isNegative()) {
            throw new InvalidRateException("amount >= 0");
        }
        if (type == null) {
            throw new InvalidRateException("type is required");
        }
        if (validPeriod == null) {
            throw new InvalidRateException("validPeriod is required");
        }
        if (strategy == null) {
            throw new InvalidRateException("strategy is required");
        }

        TariffRate rate = new TariffRate();
        rate.positionId = positionId;
        rate.amount = amount;
        rate.type = type;
        rate.validPeriod = validPeriod;
        rate.strategy = strategy;
        return rate;
    }

    public static TariffRate rehydrate(
            Long id,
            Long positionId,
            Money amount,
            RateType type,
            DateRange validPeriod,
            RateCalculationStrategy strategy
    ) {
        TariffRate rate = new TariffRate();
        rate.id = id;
        rate.positionId = positionId;
        rate.amount = amount;
        rate.type = type;
        rate.validPeriod = validPeriod;
        rate.strategy = strategy;
        return rate;
    }

    public Money calculateFor(RateCalculationContext context) {
        return strategy.baseAmount(amount, context);
    }

    public Money hourlyRate(BigDecimal normHours) {
        return strategy.hourlyRate(amount, normHours);
    }

    public boolean appliesTo(LocalDate date) {
        return validPeriod.contains(date);
    }
}