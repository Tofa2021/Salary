package com.example.backend.payroll.statutory;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.policy.IncomeTaxPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IncomeTaxPolicyTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final DateRange VALID_PERIOD = DateRange.of(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
    );

    private IncomeTaxPolicy policy;

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @BeforeEach
    void setUp() {
        policy = new IncomeTaxPolicy();
    }

    @Test
    void calculatesTaxWithoutThreshold() {
        StatutoryRate rate = StatutoryRate.create(
                "INCOME_TAX",
                Percentage.of(new BigDecimal("13")),
                null,
                VALID_PERIOD
        );

        assertThat(policy.calculate(money("2500.00"), rate)).isEqualTo(money("325.00"));
    }

    @Test
    void calculatesTaxWithThreshold() {
        StatutoryRate rate = StatutoryRate.create(
                "INCOME_TAX",
                Percentage.of(new BigDecimal("13")),
                money("600.00"),
                VALID_PERIOD
        );

        assertThat(policy.calculate(money("2500.00"), rate)).isEqualTo(money("247.00"));
    }

    @Test
    void returnsZeroWhenGrossWithinThreshold() {
        StatutoryRate rate = StatutoryRate.create(
                "INCOME_TAX",
                Percentage.of(new BigDecimal("13")),
                money("600.00"),
                VALID_PERIOD
        );

        assertThat(policy.calculate(money("600.00"), rate)).isEqualTo(money("0.00"));
        assertThat(policy.calculate(money("400.00"), rate)).isEqualTo(money("0.00"));
    }

    @Test
    void roundsTaxHalfUp() {
        StatutoryRate rate = StatutoryRate.create(
                "INCOME_TAX",
                Percentage.of(new BigDecimal("13")),
                null,
                VALID_PERIOD
        );

        assertThat(policy.calculate(money("100.50"), rate)).isEqualTo(money("13.07"));
        assertThat(policy.calculate(money("100.40"), rate)).isEqualTo(money("13.05"));
    }

    @Test
    void rejectsNullArguments() {
        StatutoryRate rate = StatutoryRate.create(
                "INCOME_TAX",
                Percentage.of(new BigDecimal("13")),
                null,
                VALID_PERIOD
        );

        assertThatThrownBy(() -> policy.calculate(null, rate))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> policy.calculate(money("1000.00"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
