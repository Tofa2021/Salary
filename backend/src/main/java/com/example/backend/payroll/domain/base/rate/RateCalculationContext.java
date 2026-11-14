package com.example.backend.payroll.domain.base.rate;

import com.example.backend.payroll.domain.base.model.SalesSummary;
import com.example.backend.payroll.domain.base.model.WorkVolumeSummary;
import com.example.backend.payroll.domain.shared.model.HoursSummary;

import java.math.BigDecimal;

public record RateCalculationContext(
        HoursSummary hours,
        BigDecimal normHours,
        WorkVolumeSummary volume,
        SalesSummary sales
) {
    public static RateCalculationContext ofHours(HoursSummary hours) {
        return new RateCalculationContext(hours, null, null, null);
    }

    public static RateCalculationContext ofMonthly(HoursSummary hours, BigDecimal normHours) {
        return new RateCalculationContext(hours, normHours, null, null);
    }

    public static RateCalculationContext ofVolume(WorkVolumeSummary volume) {
        return new RateCalculationContext(null, null, volume, null);
    }

    public static RateCalculationContext ofSales(SalesSummary sales) {
        return new RateCalculationContext(null, null, null, sales);
    }
}
