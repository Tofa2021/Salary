package com.example.backend.payroll.base;

import com.example.backend.payroll.domain.base.exception.UnsupportedRateTypeException;
import com.example.backend.payroll.domain.base.rate.*;
import com.example.backend.payroll.domain.shared.model.RateType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateCalculationStrategyRegistryTest {

    @Nested
    class DefaultRegistry {

        private final RateCalculationStrategyRegistry registry = RateCalculationStrategyRegistry.defaultRegistry();

        @Test
        void knows_all_four_rate_types() {
            assertThat(registry.get(RateType.HOURLY)).isInstanceOf(HourlyRateStrategy.class);
            assertThat(registry.get(RateType.DAILY)).isInstanceOf(DailyRateStrategy.class);
            assertThat(registry.get(RateType.MONTHLY)).isInstanceOf(MonthlyRateStrategy.class);
            assertThat(registry.get(RateType.PIECEWORK)).isInstanceOf(PieceworkRateStrategy.class);
        }

        @Test
        @Disabled
        void each_strategy_reports_matching_supports() {
            for (RateType type : RateType.values()) {
                assertThat(registry.get(type).supports()).isEqualTo(type);
            }
        }
    }

    @Nested
    class CustomRegistry {

        @Test
        void returns_registered_strategy_for_type() {
            RateCalculationStrategy fake = new RateCalculationStrategy() {
                @Override
                public RateType supports() {
                    return RateType.HOURLY;
                }

                @Override
                public com.example.backend.payroll.domain.shared.model.Money
                baseAmount(com.example.backend.payroll.domain.shared.model.Money a, RateCalculationContext c) {
                    return a;
                }

                @Override
                public com.example.backend.payroll.domain.shared.model.Money
                hourlyRate(com.example.backend.payroll.domain.shared.model.Money a, java.math.BigDecimal n) {
                    return a;
                }
            };
            var registry = new RateCalculationStrategyRegistry(List.of(fake));

            assertThat(registry.get(RateType.HOURLY)).isSameAs(fake);
        }

        @Test
        void throws_for_unknown_type() {
            var registry = new RateCalculationStrategyRegistry(
                    List.of(new HourlyRateStrategy()));

            assertThatThrownBy(() -> registry.get(RateType.MONTHLY))
                    .isInstanceOf(UnsupportedRateTypeException.class);
        }

        @Test
        void rejects_empty_list() {
            assertThatThrownBy(() -> new RateCalculationStrategyRegistry(List.of()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_null_list() {
            assertThatThrownBy(() -> new RateCalculationStrategyRegistry(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_duplicate_types() {
            assertThatThrownBy(() -> new RateCalculationStrategyRegistry(
                    List.of(new HourlyRateStrategy(), new HourlyRateStrategy())))
                    .isInstanceOf(IllegalStateException.class);
        }
    }
}
