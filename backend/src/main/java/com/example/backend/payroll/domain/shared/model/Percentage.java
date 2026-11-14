package com.example.backend.payroll.domain.shared.model;


import com.example.backend.payroll.domain.shared.exception.InvalidPercentageException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Percentage(BigDecimal value) {
    public Percentage {
        if (value == null) {
            throw new InvalidPercentageException("value is required");
        }

        value = value.stripTrailingZeros();

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidPercentageException("value >= 0");
        }
        
        if (value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidPercentageException("value <= 100");
        }
    }

    public static Percentage of(BigDecimal value) {
        return new Percentage(value);
    }

    public static Percentage of(double value) {
        return new Percentage(BigDecimal.valueOf(value));
    }

    public static Percentage zero() {
        return new Percentage(BigDecimal.ZERO);
    }

    public BigDecimal asFraction() {
        return value.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
    }
}
