package com.example.backend.payroll.base;

import com.example.backend.payroll.domain.base.rate.HourlyRateStrategy;
import com.example.backend.payroll.domain.base.rate.RateCalculationContext;
import com.example.backend.payroll.domain.shared.model.RateType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static com.example.backend.payroll.base.PayrollFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

class HourlyRateStrategyTest {
    private final HourlyRateStrategy strategy = new HourlyRateStrategy();

    @Test
    void supports_hourly_rate_type() {
        assertThat(strategy.supports()).isEqualTo(RateType.HOURLY);
    }

    @Nested
    class BaseAmount {

        @ParameterizedTest(name = "{0} BYN/ч × {1} ч = {2}")
        @CsvSource({
                "100, 0,   0",
                "100, 1,   100",
                "100, 8,   800",
                "100, 168, 16800",
                "0,   8,   0",
        })
        void multiplies_rate_by_regular_hours(String rate, String hours, String expected) {
            var ctx = RateCalculationContext.ofHours(regularHours(hours));

            var result = strategy.baseAmount(money(rate), ctx);

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void handles_fractional_hours() {
            var ctx = RateCalculationContext.ofHours(regularHours("0.5"));

            var result = strategy.baseAmount(money("100"), ctx);

            assertThat(result).isEqualTo(money("50"));
        }

        @Test
        void ignores_normHours_and_volume() {
            var ctx = new RateCalculationContext(regularHours("8"), new BigDecimal("160"), volume("999"), null);

            var result = strategy.baseAmount(money("100"), ctx);

            assertThat(result).isEqualTo(money("800"));
        }
    }

    @Nested
    class HourlyRate {

        @Test
        void returns_amount_as_is() {
            assertThat(strategy.hourlyRate(money("100"), new BigDecimal("160")))
                    .isEqualTo(money("100"));
        }

        @Test
        void ignores_normHours() {
            assertThat(strategy.hourlyRate(money("100"), null))
                    .isEqualTo(money("100"));
        }
    }
}
