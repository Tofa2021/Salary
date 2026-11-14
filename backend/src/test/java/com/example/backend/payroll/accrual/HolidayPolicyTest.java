package com.example.backend.payroll.accrual;

import com.example.backend.payroll.domain.accrual.policy.HolidayPolicy;
import com.example.backend.payroll.domain.shared.model.Money;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HolidayPolicyTest {

    private static final Money RATE = money("100");

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("BYN"));
    }

    @Nested
    class Calculation {

        @ParameterizedTest(name = "x{0} за {1} ч → {2}")
        @CsvSource({
                "2.0, 8,  1600",
                "1.0, 8,  800",
                "1.5, 8,  1200",
                "2.0, 0,  0",
                "2.0, 1,  200",
        })
        void multiplies_hourly_rate_by_hours_and_multiplier(
                String multiplier, String hours, String expected
        ) {
            HolidayPolicy policy = new HolidayPolicy(new BigDecimal(multiplier));

            Money result = policy.calculate(RATE, new BigDecimal(hours));

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void returns_zero_for_negative_hours() {
            HolidayPolicy policy = new HolidayPolicy(new BigDecimal("2.0"));

            assertThat(policy.calculate(RATE, new BigDecimal("-1")))
                    .isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }

        @Test
        void handles_fractional_hours() {
            HolidayPolicy policy = new HolidayPolicy(new BigDecimal("2.0"));

            assertThat(policy.calculate(RATE, new BigDecimal("0.5")))
                    .isEqualTo(money("100"));
        }
    }

    @Nested
    class Contract {

        @Test
        void rejects_null_hourly_rate() {
            HolidayPolicy policy = new HolidayPolicy(new BigDecimal("2.0"));

            assertThatThrownBy(() -> policy.calculate(null, BigDecimal.ONE))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_null_hours() {
            HolidayPolicy policy = new HolidayPolicy(new BigDecimal("2.0"));

            assertThatThrownBy(() -> policy.calculate(RATE, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
