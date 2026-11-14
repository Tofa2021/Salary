package com.example.backend.payroll.base;

import com.example.backend.payroll.domain.base.rate.DailyRateStrategy;
import com.example.backend.payroll.domain.base.rate.RateCalculationContext;
import com.example.backend.payroll.domain.shared.model.RateType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static com.example.backend.payroll.base.PayrollFixtures.money;
import static com.example.backend.payroll.base.PayrollFixtures.regularHours;
import static org.assertj.core.api.Assertions.assertThat;

class DailyRateStrategyTest {
    private final DailyRateStrategy strategy = new DailyRateStrategy();

    @Test
    void supports_daily_rate_type() {
        assertThat(strategy.supports()).isEqualTo(RateType.DAILY);
    }

    @Nested
    class BaseAmount {

        @ParameterizedTest(name = "{0} BYN/день × {1} ч = {2}")
        @CsvSource({
                "800,  0,   0",
                "800,  4,   400",
                "800,  8,   800",
                "800,  16,  1600",
                "0,    8,   0",
        })
        void prorates_daily_rate_by_eight_hour_day(String rate, String hours, String expected) {
            var ctx = RateCalculationContext.ofHours(regularHours(hours));

            var result = strategy.baseAmount(money(rate), ctx);

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void handles_non_integer_days() {
            var ctx = RateCalculationContext.ofHours(regularHours("12"));

            var result = strategy.baseAmount(money("800"), ctx);

            assertThat(result).isEqualTo(money("1200"));
        }

        @Test
        void rounds_hours_to_days_to_two_decimals() {
            var ctx = RateCalculationContext.ofHours(regularHours("1"));

            var result = strategy.baseAmount(money("800"), ctx);

            assertThat(result).isEqualTo(money("104"));
        }
    }

    @Nested
    class HourlyRate {

        @Test
        void divides_daily_amount_by_eight() {
            assertThat(strategy.hourlyRate(money("800"), new BigDecimal("160")))
                    .isEqualTo(money("100"));
        }

        @Test
        void ignores_normHours() {
            assertThat(strategy.hourlyRate(money("800"), null))
                    .isEqualTo(money("100"));
        }
    }
}
