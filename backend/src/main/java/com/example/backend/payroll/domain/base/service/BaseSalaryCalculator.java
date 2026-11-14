package com.example.backend.payroll.domain.base.service;

import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.base.model.SalesSummary;
import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.model.WorkVolumeSummary;
import com.example.backend.payroll.domain.base.rate.RateCalculationContext;
import com.example.backend.payroll.domain.base.rate.RateCalculationStrategyRegistry;
import com.example.backend.payroll.domain.shared.model.HoursSummary;
import com.example.backend.payroll.domain.shared.model.Money;

import java.math.BigDecimal;

public class BaseSalaryCalculator {
    private final RateCalculationStrategyRegistry registry;

    public BaseSalaryCalculator(RateCalculationStrategyRegistry registry) {
        this.registry = registry;
    }

    public Money calculate(
            TariffRate rate,
            HoursSummary hours,
            WorkVolumeSummary volume,
            SalesSummary sales,
            BigDecimal normHours
    ) {
        if (rate == null) {
            throw new InvalidRateException("rate is required");
        }
        if (hours == null) {
            throw new InvalidRateException("hours is required");
        }

        RateCalculationContext context = new RateCalculationContext(hours, normHours, volume, sales);
        return rate.calculateFor(context);
    }
}