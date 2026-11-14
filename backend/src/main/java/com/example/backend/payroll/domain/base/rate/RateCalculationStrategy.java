package com.example.backend.payroll.domain.base.rate;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.RateType;

import java.math.BigDecimal;

public interface RateCalculationStrategy {
    RateType supports();

    Money baseAmount(Money amount, RateCalculationContext context);

    Money hourlyRate(Money amount, BigDecimal normHours);
}