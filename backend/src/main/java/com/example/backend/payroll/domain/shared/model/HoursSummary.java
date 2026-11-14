package com.example.backend.payroll.domain.shared.model;


import com.example.backend.payroll.domain.shared.exception.InvalidHoursSummaryException;

import java.math.BigDecimal;

public record HoursSummary(
        BigDecimal regular,
        BigDecimal overtime,
        BigDecimal night,
        BigDecimal holiday
) {
    public HoursSummary {
        if (regular == null) {
            regular = BigDecimal.ZERO;
        }
        if (overtime == null) {
            overtime = BigDecimal.ZERO;
        }
        if (night == null) {
            night = BigDecimal.ZERO;
        }
        if (holiday == null) {
            holiday = BigDecimal.ZERO;
        }
        if (regular.signum() < 0 || overtime.signum() < 0 || night.signum() < 0) {
            throw new InvalidHoursSummaryException("hours must be >= 0");
        }
    }

    public static HoursSummary of(BigDecimal regular, BigDecimal overtime, BigDecimal night, BigDecimal holiday) {
        return new HoursSummary(regular, overtime, night, holiday);
    }

    public BigDecimal total() {
        return regular.add(overtime).add(night);
    }
}
