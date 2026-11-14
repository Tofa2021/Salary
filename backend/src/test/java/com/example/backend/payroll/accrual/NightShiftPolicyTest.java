package com.example.backend.payroll.accrual;

import com.example.backend.payroll.domain.accrual.policy.NightShiftPolicy;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NightShiftPolicyTest {

    private static final Money RATE = money("100");

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("BYN"));
    }

    @Nested
    class Calculation {

        @ParameterizedTest(name = "{0}% надбавки, {1} ч → {2}")
        @CsvSource({
                "20,  10,  1200",
                "40,  10,  1400",
                "0,   10,  1000",
                "100, 10,  2000",
        })
        void adds_percentage_surcharge_over_base(
                String percent, String hours, String expected
        ) {
            NightShiftPolicy policy = new NightShiftPolicy(Percentage.of(new BigDecimal(percent)));

            Money result = policy.calculate(RATE, new BigDecimal(hours));

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void returns_zero_for_zero_hours() {
            NightShiftPolicy policy = new NightShiftPolicy(Percentage.of(20));

            assertThat(policy.calculate(RATE, BigDecimal.ZERO))
                    .isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }

        @Test
        void returns_zero_for_negative_hours() {
            NightShiftPolicy policy = new NightShiftPolicy(Percentage.of(20));

            assertThat(policy.calculate(RATE, new BigDecimal("-5")))
                    .isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }

        @Test
        void handles_fractional_hours() {
            NightShiftPolicy policy = new NightShiftPolicy(Percentage.of(20));

            assertThat(policy.calculate(RATE, new BigDecimal("0.5")))
                    .isEqualTo(money("60"));
        }
    }

    @Nested
    class Contract {

        @Test
        void rejects_null_hourly_rate() {
            NightShiftPolicy policy = new NightShiftPolicy(Percentage.of(20));

            assertThatThrownBy(() -> policy.calculate(null, BigDecimal.ONE))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void rejects_null_hours() {
            NightShiftPolicy policy = new NightShiftPolicy(Percentage.of(20));

            assertThatThrownBy(() -> policy.calculate(RATE, null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class Invariants {

        @Test
        void surcharge_is_never_less_than_base() {
            for (String p : new String[]{"0", "1", "20", "50", "100"}) {
                NightShiftPolicy policy =
                        new NightShiftPolicy(Percentage.of(new BigDecimal(p)));
                Money base = RATE.multiply(new BigDecimal("10"));
                Money result = policy.calculate(RATE, new BigDecimal("10"));

                assertThat(result.amount())
                        .as("percent=%s", p)
                        .isGreaterThanOrEqualTo(base.amount());
            }
        }
    }
}
