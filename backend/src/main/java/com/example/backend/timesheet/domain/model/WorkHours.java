package com.example.backend.timesheet.domain.model;

import com.example.backend.timesheet.domain.exception.hours.InvalidWorkHoursException;

import java.math.BigDecimal;

public record WorkHours(BigDecimal value) {
    public WorkHours {
        if (value == null) {
            throw new InvalidWorkHoursException("hours is required");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidWorkHoursException("hours must be >= 0");
        }
        if (value.compareTo(BigDecimal.valueOf(24)) > 0) {
            throw new InvalidWorkHoursException("hours must be <= 24");
        }
    }

    public static WorkHours of(double hours) {
        return new WorkHours(BigDecimal.valueOf(hours));
    }

    public WorkHours add(WorkHours other) {
        return new WorkHours(this.value.add(other.value));
    }
}
