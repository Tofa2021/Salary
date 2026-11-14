package com.example.backend.payroll.domain.accrual.policy;

import com.example.backend.payroll.domain.shared.model.Money;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPolicy {
    private final BigDecimal multiplier;

    public Money calculate(Money hourlyRate, BigDecimal holidayHours) {
        if (hourlyRate == null) {
            throw new IllegalArgumentException("hourlyRate is required");
        }
        if (holidayHours == null) {
            throw new IllegalArgumentException("holidayHours is required");
        }

        if (holidayHours.signum() <= 0) {
            return Money.zero(hourlyRate.currency());
        }
        return hourlyRate.multiply(holidayHours).multiply(multiplier);
    }
}
