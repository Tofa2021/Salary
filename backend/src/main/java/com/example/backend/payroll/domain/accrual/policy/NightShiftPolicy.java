package com.example.backend.payroll.domain.accrual.policy;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class NightShiftPolicy {
    private final Percentage nightRate;

    public Money calculate(Money hourlyRate, BigDecimal nightHours) {
        if (hourlyRate == null) {
            throw new IllegalArgumentException("hourlyRate is required");
        }
        if (nightHours == null) {
            throw new IllegalArgumentException("nightHours is required");
        }
        if (nightHours.signum() <= 0) {
            return Money.zero(hourlyRate.currency());
        }
        Money base = hourlyRate.multiply(nightHours);
        return base.add(base.multiply(nightRate));
    }
}
