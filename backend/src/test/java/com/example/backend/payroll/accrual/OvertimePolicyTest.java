package com.example.backend.payroll.accrual;

import com.example.backend.payroll.domain.accrual.policy.OvertimePolicy;
import com.example.backend.payroll.domain.shared.model.Money;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OvertimePolicyTest {
    private static final Money RATE = money("100");
    private final OvertimePolicy policy = new OvertimePolicy(new BigDecimal("1.5"), new BigDecimal("2.0"));

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("BYN"));
    }

    @Nested
    class Calculation {

        @ParameterizedTest(name = "{0} ч → {1}")
        @CsvSource({
                "0,     0",
                "1,     150",
                "2,     300",
                "3,     500",
                "4,     700",
                "10,    1900",
        })
        void pays_first_two_hours_at_first_multiplier_and_rest_at_subsequent(
                String hours, String expected
        ) {
            Money result = policy.calculate(RATE, new BigDecimal(hours));

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void returns_zero_for_negative_hours() {
            Money result = policy.calculate(RATE, new BigDecimal("-3"));

            assertThat(result).isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }

        @Test
        void returns_zero_for_zero_hours() {
            Money result = policy.calculate(RATE, BigDecimal.ZERO);

            assertThat(result).isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }

        @Test
        void handles_fractional_hours() {
            Money result = policy.calculate(RATE, new BigDecimal("1.5"));

            assertThat(result).isEqualTo(money("225"));
        }

        @Test
        void handles_fractional_hours_crossing_threshold() {
            Money result = policy.calculate(RATE, new BigDecimal("2.5"));

            assertThat(result).isEqualTo(money("400"));
        }
    }

    @Nested
    class Contract {

        @Test
        void rejects_null_hourly_rate() {
            assertThatThrownBy(() -> policy.calculate(null, BigDecimal.ONE))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_null_hours() {
            assertThatThrownBy(() -> policy.calculate(RATE, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_non_positive_first_multiplier() {
            assertThatThrownBy(() -> new OvertimePolicy(BigDecimal.ZERO, BigDecimal.ONE))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_negative_subsequent_multiplier() {
            assertThatThrownBy(() -> new OvertimePolicy(BigDecimal.ONE, new BigDecimal("-1")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Invariants {

        @Test
        void result_is_never_negative() {
            for (String h : new String[]{"-10", "-0.1", "0", "0.1", "1", "2", "3", "100"}) {
                Money result = policy.calculate(RATE, new BigDecimal(h));
                assertThat(result.isNegative())
                        .as("hours=%s", h)
                        .isFalse();
            }
        }

        @Test
        void result_is_monotonic_in_hours() {
            BigDecimal prev = BigDecimal.ZERO;
            for (String h : new String[]{"0", "0.5", "1", "1.5", "2", "2.5", "3", "10"}) {
                Money result = policy.calculate(RATE, new BigDecimal(h));
                BigDecimal amount = result.amount(); // подставь свой геттер
                assertThat(amount)
                        .as("hours=%s", h)
                        .isGreaterThanOrEqualTo(prev);
                prev = amount;
            }
        }
    }
}
