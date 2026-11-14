package com.example.backend.payroll.employer;

import com.example.backend.payroll.domain.employer.exception.InvalidEmployerContributionException;
import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployerContributionTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 12, 31);
    private static final DateRange VALID_PERIOD = DateRange.of(START, END);

    @Nested
    class Creation {

        @Test
        void createsValidContribution() {
            Percentage rate = Percentage.of(new BigDecimal("34"));

            EmployerContribution contribution = EmployerContribution.create(
                    "FSZN_EMPLOYER",
                    rate,
                    VALID_PERIOD
            );

            assertThat(contribution.getId()).isNull();
            assertThat(contribution.getCode()).isEqualTo("FSZN_EMPLOYER");
            assertThat(contribution.getRate()).isEqualTo(rate);
            assertThat(contribution.getValidPeriod()).isEqualTo(VALID_PERIOD);
        }

        @Test
        void rehydratesContribution() {
            Percentage rate = Percentage.of(new BigDecimal("0.6"));

            EmployerContribution contribution = EmployerContribution.rehydrate(
                    77L,
                    "BELGOSSTRAKH",
                    rate,
                    VALID_PERIOD
            );

            assertThat(contribution.getId()).isEqualTo(77L);
            assertThat(contribution.getCode()).isEqualTo("BELGOSSTRAKH");
            assertThat(contribution.getRate()).isEqualTo(rate);
            assertThat(contribution.getValidPeriod()).isEqualTo(VALID_PERIOD);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        void rejectsBlankCode(String invalidCode) {
            Percentage rate = Percentage.of(new BigDecimal("34"));

            assertThatThrownBy(() -> EmployerContribution.create(invalidCode, rate, VALID_PERIOD))
                    .isInstanceOf(InvalidEmployerContributionException.class);
        }

        @Test
        void rejectsNullRateOrPeriod() {
            Percentage rate = Percentage.of(new BigDecimal("34"));

            assertThatThrownBy(() -> EmployerContribution.create("FSZN_EMPLOYER", null, VALID_PERIOD))
                    .isInstanceOf(InvalidEmployerContributionException.class);
            assertThatThrownBy(() -> EmployerContribution.create("FSZN_EMPLOYER", rate, null))
                    .isInstanceOf(InvalidEmployerContributionException.class);
        }
    }

    @Nested
    class Applicability {

        @Test
        void appliesToDatesWithinRange() {
            EmployerContribution contribution = EmployerContribution.create(
                    "FSZN_EMPLOYER",
                    Percentage.of(new BigDecimal("34")),
                    VALID_PERIOD
            );

            assertThat(contribution.appliesTo(START)).isTrue();
            assertThat(contribution.appliesTo(LocalDate.of(2026, 7, 15))).isTrue();
            assertThat(contribution.appliesTo(END)).isTrue();
            assertThat(contribution.appliesTo(START.minusDays(1))).isFalse();
            assertThat(contribution.appliesTo(END.plusDays(1))).isFalse();
        }
    }
}
