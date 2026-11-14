package com.example.backend.payroll.domain.base.rate;

import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.RateType;

import java.math.BigDecimal;

public class PieceworkRateStrategy implements RateCalculationStrategy {
    @Override
    public RateType supports() {
        return RateType.PIECEWORK;
    }

    @Override
    public Money baseAmount(Money amount, RateCalculationContext ctx) {
        if (ctx.volume() == null || ctx.volume().units().signum() < 0) {
            throw new InvalidRateException("volume required for PIECEWORK");
        }
        return amount.multiply(ctx.volume().units());
    }

    @Override
    public Money hourlyRate(Money amount, BigDecimal normHours) {
        throw new InvalidRateException("hourly rate not supported for PIECEWORK");
    }
}
