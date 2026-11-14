package com.example.backend.payroll.domain.base.rate;


import com.example.backend.payroll.domain.base.exception.UnsupportedRateTypeException;
import com.example.backend.payroll.domain.shared.model.RateType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RateCalculationStrategyRegistry {
    private final Map<RateType, RateCalculationStrategy> strategies;

    public RateCalculationStrategyRegistry(List<RateCalculationStrategy> strategies) {
        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException("strategies are required");
        }
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(RateCalculationStrategy::supports, s -> s));
    }

    public static RateCalculationStrategyRegistry defaultRegistry() {
        return new RateCalculationStrategyRegistry(List.of(
                new MonthlyRateStrategy(),
                new HourlyRateStrategy(),
                new DailyRateStrategy(),
                new PieceworkRateStrategy()
        ));
    }

    public RateCalculationStrategy get(RateType type) {
        RateCalculationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new UnsupportedRateTypeException(type);
        }
        return strategy;
    }
}