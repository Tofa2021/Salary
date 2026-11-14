package com.example.backend.payroll.domain.base.rate;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.RateType;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class DailyRateStrategy implements RateCalculationStrategy {
    @Override
    public RateType supports() {
        return RateType.DAILY;
    }

    @Override
    public Money baseAmount(Money amount, RateCalculationContext ctx) {
        return amount.multiply(ctx.hours().regular().divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP));
    }

    @Override
    public Money hourlyRate(Money amount, BigDecimal normHours) {
        return amount.divide(8);
    }
}
