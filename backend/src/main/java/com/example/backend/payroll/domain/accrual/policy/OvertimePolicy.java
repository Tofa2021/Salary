package com.example.backend.payroll.domain.accrual.policy;

import com.example.backend.payroll.domain.shared.model.Money;

import java.math.BigDecimal;

public class OvertimePolicy {
    private static final BigDecimal TWO_HOURS_THRESHOLD = BigDecimal.valueOf(2);
    private final BigDecimal firstTwoMultiplier;
    private final BigDecimal subsequentMultiplier;

    public OvertimePolicy(BigDecimal firstTwoMultiplier, BigDecimal subsequentMultiplier) {
        if (firstTwoMultiplier == null || firstTwoMultiplier.signum() <= 0) {
            throw new IllegalArgumentException("firstTwoMultiplier > 0");
        }
        if (subsequentMultiplier == null || subsequentMultiplier.signum() <= 0) {
            throw new IllegalArgumentException("subsequentMultiplier > 0");
        }
        this.firstTwoMultiplier = firstTwoMultiplier;
        this.subsequentMultiplier = subsequentMultiplier;
    }

    public Money calculate(Money hourlyRate, BigDecimal overtimeHours) {
        if (hourlyRate == null || overtimeHours == null) {
            throw new IllegalArgumentException("hourlyRate and overtimeHours are required");
        }
        if (overtimeHours.signum() <= 0) return Money.zero(hourlyRate.currency());

        BigDecimal first = overtimeHours.min(TWO_HOURS_THRESHOLD);
        BigDecimal rest = overtimeHours.subtract(first).max(BigDecimal.ZERO);

        return hourlyRate.multiply(first).multiply(firstTwoMultiplier)
                .add(hourlyRate.multiply(rest).multiply(subsequentMultiplier));
    }
}
