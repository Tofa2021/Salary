package com.example.backend.payroll.domain.base.rate;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.RateType;

import java.math.BigDecimal;

public class HourlyRateStrategy implements RateCalculationStrategy {
    @Override
    public RateType supports() {
        return RateType.HOURLY;
    }

    @Override
    public Money baseAmount(Money amount, RateCalculationContext ctx) {
        return amount.multiply(ctx.hours().regular());
    }

    @Override
    public Money hourlyRate(Money amount, BigDecimal normHours) {
        return amount;
    }
}
