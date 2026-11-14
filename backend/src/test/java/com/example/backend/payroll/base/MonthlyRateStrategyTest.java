package com.example.backend.payroll.base;

import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.base.rate.MonthlyRateStrategy;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MonthlyRateStrategyTest {
    private final MonthlyRateStrategy strategy = new MonthlyRateStrategy();

    @Test
    void supports_monthly_rate_type() {
        assertThat(strategy.supports()).isEqualTo(RateType.MONTHLY);
    }

    @Nested
    class BaseAmount {

        @ParameterizedTest(name = "{0} BYN/мес, норма {1} ч, отработано {2} ч = {3}")
        @CsvSource({
                "16800, 168, 0,    0",
                "16800, 168, 84,   8400",
                "16800, 168, 168,  16800",
                "16800, 168, 336,  33600",
                "0,     168, 168,  0",
        })
        void prorates_monthly_rate_by_share_of_norm(String amount, String norm, String hours, String expected) {
            var ctx = RateCalculationContext.ofMonthly(regularHours(hours), new BigDecimal(norm));

            var result = strategy.baseAmount(money(amount), ctx);

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void handles_fractional_hours() {
            var ctx = RateCalculationContext.ofMonthly(
                    regularHours("84.5"), new BigDecimal("169"));

            var result = strategy.baseAmount(money("16800"), ctx);

            assertThat(result).isEqualTo(money("8400"));
        }

        @Test
        void rejects_null_norm() {
            var ctx = RateCalculationContext.ofMonthly(regularHours("168"), null);

            assertThatThrownBy(() -> strategy.baseAmount(money("16800"), ctx))
                    .isInstanceOf(InvalidRateException.class);
        }

        @Test
        void rejects_zero_norm() {
            var ctx = RateCalculationContext.ofMonthly(regularHours("168"), BigDecimal.ZERO);

            assertThatThrownBy(() -> strategy.baseAmount(money("16800"), ctx))
                    .isInstanceOf(InvalidRateException.class);
        }

        @Test
        void rejects_negative_norm() {
            var ctx = RateCalculationContext.ofMonthly(regularHours("168"), new BigDecimal("-1"));

            assertThatThrownBy(() -> strategy.baseAmount(money("16800"), ctx))
                    .isInstanceOf(InvalidRateException.class);
        }
    }

    @Nested
    class HourlyRate {

        @Test
        void divides_monthly_amount_by_norm() {
            assertThat(strategy.hourlyRate(money("16800"), new BigDecimal("168")))
                    .isEqualTo(money("100"));
        }

        @Test
        void rejects_null_norm() {
            assertThatThrownBy(() -> strategy.hourlyRate(money("16800"), null))
                    .isInstanceOf(InvalidRateException.class);
        }

        @Test
        void rejects_non_positive_norm() {
            assertThatThrownBy(() -> strategy.hourlyRate(money("16800"), BigDecimal.ZERO))
                    .isInstanceOf(InvalidRateException.class);
            assertThatThrownBy(() -> strategy.hourlyRate(money("16800"), new BigDecimal("-1")))
                    .isInstanceOf(InvalidRateException.class);
        }
    }
}
