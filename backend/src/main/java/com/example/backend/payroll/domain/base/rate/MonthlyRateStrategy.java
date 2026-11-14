package com.example.backend.payroll.domain.base.rate;

import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.RateType;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MonthlyRateStrategy implements RateCalculationStrategy {

    @Override
    public RateType supports() {
        return RateType.MONTHLY;
    }

    @Override
    public Money baseAmount(Money amount, RateCalculationContext ctx) {
        requireNorm(ctx.normHours());
        return amount.multiply(ctx.hours().regular()
                .divide(ctx.normHours(), 4, RoundingMode.HALF_UP));
    }

    @Override
    public Money hourlyRate(Money amount, BigDecimal normHours) {
        requireNorm(normHours);
        return amount.divide(normHours);
    }

    private void requireNorm(BigDecimal normHours) {
        if (normHours == null || normHours.signum() <= 0) {
            throw new InvalidRateException("normHours required for MONTHLY");
        }
    }
}
