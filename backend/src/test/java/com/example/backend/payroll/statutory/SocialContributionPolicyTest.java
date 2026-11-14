package com.example.backend.payroll.statutory;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.policy.SocialContributionPolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SocialContributionPolicyTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final DateRange VALID_PERIOD = DateRange.of(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
    );

    private SocialContributionPolicy policy;

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @BeforeEach
    void setUp() {
        policy = new SocialContributionPolicy();
    }

    @Test
    void calculatesOnFullGross() {
        StatutoryRate rate = StatutoryRate.create(
                "SOCIAL_EMPLOYEE",
                Percentage.of(new BigDecimal("1")),
                money("500.00"),
                VALID_PERIOD
        );

        assertThat(policy.calculate(money("2500.00"), rate)).isEqualTo(money("25.00"));
    }

    @Test
    void roundsSocialContributionHalfUp() {
        StatutoryRate rate = StatutoryRate.create(
                "SOCIAL_EMPLOYEE",
                Percentage.of(new BigDecimal("1")),
                null,
                VALID_PERIOD
        );

        assertThat(policy.calculate(money("100.50"), rate)).isEqualTo(money("1.01"));
        assertThat(policy.calculate(money("100.49"), rate)).isEqualTo(money("1.00"));
    }

    @Test
    void returnsZeroForZeroGross() {
        StatutoryRate rate = StatutoryRate.create(
                "SOCIAL_EMPLOYEE",
                Percentage.of(new BigDecimal("1")),
                null,
                VALID_PERIOD
        );

        assertThat(policy.calculate(money("0.00"), rate)).isEqualTo(money("0.00"));
    }

    @Test
    void rejectsNullArguments() {
        StatutoryRate rate = StatutoryRate.create(
                "SOCIAL_EMPLOYEE",
                Percentage.of(new BigDecimal("1")),
                null,
                VALID_PERIOD
        );

        assertThatThrownBy(() -> policy.calculate(null, rate))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> policy.calculate(money("1000.00"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
