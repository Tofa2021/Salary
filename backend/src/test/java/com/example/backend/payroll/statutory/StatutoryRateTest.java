package com.example.backend.payroll.statutory;

import com.example.backend.payroll.domain.shared.exception.InvalidMoneyException;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import com.example.backend.payroll.domain.statutory.exception.InvalidStatutoryRateException;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatutoryRateTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final Currency USD = Currency.getInstance("USD");
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 12, 31);
    private static final DateRange VALID_PERIOD = DateRange.of(START, END);

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @Nested
    class Creation {

        @Test
        void createsWithThreshold() {
            Percentage rate = Percentage.of(new BigDecimal("13"));
            Money threshold = money("600.00");

            StatutoryRate statutoryRate = StatutoryRate.create("INCOME_TAX", rate, threshold, VALID_PERIOD);

            assertThat(statutoryRate.getId()).isNull();
            assertThat(statutoryRate.getCode()).isEqualTo("INCOME_TAX");
            assertThat(statutoryRate.getRate()).isEqualTo(rate);
            assertThat(statutoryRate.getThreshold()).isEqualTo(threshold);
            assertThat(statutoryRate.getValidPeriod()).isEqualTo(VALID_PERIOD);
        }

        @Test
        void createsWithNullOrZeroThreshold() {
            Percentage rate = Percentage.of(new BigDecimal("1"));

            StatutoryRate withoutThreshold = StatutoryRate.create("SOCIAL_EMPLOYEE", rate, null, VALID_PERIOD);
            assertThat(withoutThreshold.getThreshold()).isNull();

            StatutoryRate zeroThreshold = StatutoryRate.create("SOCIAL_EMPLOYEE", rate, money("0.00"), VALID_PERIOD);
            assertThat(zeroThreshold.getThreshold()).isEqualTo(money("0.00"));
        }

        @Test
        void rehydratesRate() {
            Percentage rate = Percentage.of(new BigDecimal("13"));
            Money threshold = money("500.00");

            StatutoryRate statutoryRate = StatutoryRate.rehydrate(12L, "INCOME_TAX", rate, threshold, VALID_PERIOD);

            assertThat(statutoryRate.getId()).isEqualTo(12L);
            assertThat(statutoryRate.getCode()).isEqualTo("INCOME_TAX");
            assertThat(statutoryRate.getRate()).isEqualTo(rate);
            assertThat(statutoryRate.getThreshold()).isEqualTo(threshold);
            assertThat(statutoryRate.getValidPeriod()).isEqualTo(VALID_PERIOD);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        void rejectsBlankCode(String invalidCode) {
            assertThatThrownBy(() -> StatutoryRate.create(
                    invalidCode, Percentage.of(new BigDecimal("13")), null, VALID_PERIOD
            )).isInstanceOf(InvalidStatutoryRateException.class);
        }

        @Test
        void rejectsNullRateOrPeriod() {
            Percentage rate = Percentage.of(new BigDecimal("13"));

            assertThatThrownBy(() -> StatutoryRate.create("INCOME_TAX", null, null, VALID_PERIOD))
                    .isInstanceOf(InvalidStatutoryRateException.class);
            assertThatThrownBy(() -> StatutoryRate.create("INCOME_TAX", rate, null, null))
                    .isInstanceOf(InvalidStatutoryRateException.class);
        }

        @Test
        void rejectsNegativeThreshold() {
            assertThatThrownBy(() -> StatutoryRate.create(
                    "INCOME_TAX",
                    Percentage.of(new BigDecimal("13")),
                    money("-0.01"),
                    VALID_PERIOD
            )).isInstanceOf(InvalidStatutoryRateException.class);
        }
    }

    @Nested
    class Applicability {

        @Test
        void appliesToDatesWithinRange() {
            StatutoryRate statutoryRate = StatutoryRate.create(
                    "INCOME_TAX",
                    Percentage.of(new BigDecimal("13")),
                    null,
                    VALID_PERIOD
            );

            assertThat(statutoryRate.appliesTo(START)).isTrue();
            assertThat(statutoryRate.appliesTo(LocalDate.of(2026, 6, 15))).isTrue();
            assertThat(statutoryRate.appliesTo(END)).isTrue();
            assertThat(statutoryRate.appliesTo(START.minusDays(1))).isFalse();
            assertThat(statutoryRate.appliesTo(END.plusDays(1))).isFalse();
        }
    }

    @Nested
    class TaxableBase {

        @Test
        void returnsFullGrossWhenNoThreshold() {
            StatutoryRate statutoryRate = StatutoryRate.create(
                    "INCOME_TAX",
                    Percentage.of(new BigDecimal("13")),
                    null,
                    VALID_PERIOD
            );

            Money gross = money("2500.00");

            assertThat(statutoryRate.taxableBase(gross)).isEqualTo(gross);
        }

        @Test
        void subtractsThresholdWhenGrossIsGreater() {
            StatutoryRate statutoryRate = StatutoryRate.create(
                    "INCOME_TAX",
                    Percentage.of(new BigDecimal("13")),
                    money("600.00"),
                    VALID_PERIOD
            );

            assertThat(statutoryRate.taxableBase(money("2500.00"))).isEqualTo(money("1900.00"));
            assertThat(statutoryRate.taxableBase(money("600.01"))).isEqualTo(money("0.01"));
        }

        @Test
        void returnsZeroWhenGrossNotGreaterThanThreshold() {
            StatutoryRate statutoryRate = StatutoryRate.create(
                    "INCOME_TAX",
                    Percentage.of(new BigDecimal("13")),
                    money("600.00"),
                    VALID_PERIOD
            );

            assertThat(statutoryRate.taxableBase(money("600.00"))).isEqualTo(money("0.00"));
            assertThat(statutoryRate.taxableBase(money("450.00"))).isEqualTo(money("0.00"));
            assertThat(statutoryRate.taxableBase(money("0.00"))).isEqualTo(money("0.00"));
        }

        @Test
        void rejectsCurrencyMismatch() {
            StatutoryRate statutoryRate = StatutoryRate.create(
                    "INCOME_TAX",
                    Percentage.of(new BigDecimal("13")),
                    money("600.00"),
                    VALID_PERIOD
            );
            Money grossUsd = Money.of(new BigDecimal("1000.00"), USD);

            assertThatThrownBy(() -> statutoryRate.taxableBase(grossUsd))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }
}
