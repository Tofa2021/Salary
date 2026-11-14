package com.example.backend.payroll.domain.base.model;

import com.example.backend.payroll.domain.base.exception.InvalidWorkNormException;
import com.example.backend.payroll.domain.shared.model.Period;

import java.math.BigDecimal;

public record WorkNorm(int year, int month, BigDecimal normHours) {
    public WorkNorm {
        if (month < 1 || month > 12) {
            throw new InvalidWorkNormException("month must be 1..12");
        }
        if (year < 1900 || year > 9999) {
            throw new InvalidWorkNormException("year out of range");
        }
        if (normHours == null) {
            throw new InvalidWorkNormException("normHours is required");
        }
        if (normHours.signum() <= 0) {
            throw new InvalidWorkNormException("normHours must be > 0");
        }
    }

    public static WorkNorm of(int year, int month, BigDecimal normHours) {
        return new WorkNorm(year, month, normHours);
    }

    public static WorkNorm of(Period period, BigDecimal normHours) {
        return new WorkNorm(period.year(), period.month(), normHours);
    }

    public Period period() {
        return Period.of(year, month);
    }

    public boolean appliesTo(Period other) {
        return other != null && year == other.year() && month == other.month();
    }
}
