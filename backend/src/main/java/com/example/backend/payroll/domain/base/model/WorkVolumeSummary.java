package com.example.backend.payroll.domain.base.model;

import com.example.backend.payroll.domain.base.exception.InvalidWorkVolumeException;
import com.example.backend.payroll.domain.shared.model.Period;

import java.math.BigDecimal;

public record WorkVolumeSummary(
        Long employeeId,
        Period period,
        BigDecimal units,
        String unit
) {
    public WorkVolumeSummary {
        if (employeeId == null) {
            throw new InvalidWorkVolumeException("employeeId is required");
        }
        if (period == null) {
            throw new InvalidWorkVolumeException("period is required");
        }
        if (units == null) {
            throw new InvalidWorkVolumeException("units is required");
        }
        if (units.signum() < 0) {
            throw new InvalidWorkVolumeException("units must be >= 0");
        }
    }

    public static WorkVolumeSummary of(Long employeeId, Period period, BigDecimal units) {
        return new WorkVolumeSummary(employeeId, period, units, null);
    }

    public static WorkVolumeSummary of(Long employeeId, Period period, BigDecimal units, String unit) {
        return new WorkVolumeSummary(employeeId, period, units, unit);
    }

    public boolean isEmpty() {
        return units.signum() == 0;
    }
}
