package com.example.backend.payroll.employer;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.model.EmployerLine;
import com.example.backend.payroll.domain.employer.model.EmployerResult;
import com.example.backend.payroll.domain.employer.policy.EmployerContributionPolicy;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmployerContributionPolicyTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final LocalDate CALC_DATE = LocalDate.of(2026, 10, 31);
    private static final DateRange ACTIVE_RANGE = DateRange.of(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
    );

    private EmployerContributionPolicy policy;

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @BeforeEach
    void setUp() {
        policy = new EmployerContributionPolicy();
    }

    @Test
    void rejectsNullGross() {
        assertThatThrownBy(() -> policy.calculate(null, List.of(), CALC_DATE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void returnsZeroContributionsWhenNoneProvided() {
        Money gross = money("2500.00");

        EmployerResult fromNull = policy.calculate(gross, null, CALC_DATE);
        assertThat(fromNull.lines()).isEmpty();
        assertThat(fromNull.totalContributions()).isEqualTo(money("0.00"));
        assertThat(fromNull.employerCost()).isEqualTo(gross);

        EmployerResult fromEmpty = policy.calculate(gross, List.of(), CALC_DATE);
        assertThat(fromEmpty.lines()).isEmpty();
        assertThat(fromEmpty.totalContributions()).isEqualTo(money("0.00"));
        assertThat(fromEmpty.employerCost()).isEqualTo(gross);
    }

    @Test
    void calculatesMultipleContributions() {
        EmployerContribution fszn = EmployerContribution.create(
                "FSZN_EMPLOYER",
                Percentage.of(new BigDecimal("34")),
                ACTIVE_RANGE
        );
        EmployerContribution belgosstrakh = EmployerContribution.create(
                "BELGOSSTRAKH",
                Percentage.of(new BigDecimal("0.6")),
                ACTIVE_RANGE
        );
        EmployerContribution customFund = EmployerContribution.create(
                "CORPORATE_PENSION",
                Percentage.of(new BigDecimal("2")),
                ACTIVE_RANGE
        );

        EmployerResult result = policy.calculate(
                money("2000.00"),
                List.of(fszn, belgosstrakh, customFund),
                CALC_DATE
        );

        assertThat(result.lines())
                .extracting(EmployerLine::code, EmployerLine::amount)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("FSZN_EMPLOYER", money("680.00")),
                        org.assertj.core.groups.Tuple.tuple("BELGOSSTRAKH", money("12.00")),
                        org.assertj.core.groups.Tuple.tuple("CORPORATE_PENSION", money("40.00"))
                );
        assertThat(result.lines())
                .extracting(EmployerLine::description)
                .allMatch(desc -> desc != null && !desc.isBlank());
        assertThat(result.totalContributions()).isEqualTo(money("732.00"));
        assertThat(result.employerCost()).isEqualTo(money("2732.00"));
    }

    @Test
    void filtersInactiveContributions() {
        EmployerContribution expired = EmployerContribution.create(
                "OLD_TAX",
                Percentage.of(new BigDecimal("5")),
                DateRange.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 9, 30))
        );
        EmployerContribution future = EmployerContribution.create(
                "FUTURE_TAX",
                Percentage.of(new BigDecimal("3")),
                DateRange.of(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 31))
        );
        EmployerContribution active = EmployerContribution.create(
                "FSZN_EMPLOYER",
                Percentage.of(new BigDecimal("34")),
                ACTIVE_RANGE
        );

        EmployerResult result = policy.calculate(
                money("1000.00"),
                List.of(expired, active, future),
                CALC_DATE
        );

        assertThat(result.lines())
                .extracting(EmployerLine::code, EmployerLine::amount)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("FSZN_EMPLOYER", money("340.00"))
                );
        assertThat(result.totalContributions()).isEqualTo(money("340.00"));
        assertThat(result.employerCost()).isEqualTo(money("1340.00"));
    }

    @Test
    void roundsEachLineBeforeSumming() {
        EmployerContribution first = EmployerContribution.create(
                "BELGOSSTRAKH",
                Percentage.of(new BigDecimal("0.6")),
                ACTIVE_RANGE
        );
        EmployerContribution second = EmployerContribution.create(
                "OTHER",
                Percentage.of(new BigDecimal("0.6")),
                ACTIVE_RANGE
        );

        EmployerResult result = policy.calculate(
                money("100.83"),
                List.of(first, second),
                CALC_DATE
        );

        assertThat(result.totalContributions()).isEqualTo(money("1.20"));
        assertThat(result.employerCost()).isEqualTo(money("102.03"));
    }

    @Test
    void handlesZeroGross() {
        EmployerContribution fszn = EmployerContribution.create(
                "FSZN_EMPLOYER",
                Percentage.of(new BigDecimal("34")),
                ACTIVE_RANGE
        );

        EmployerResult result = policy.calculate(money("0.00"), List.of(fszn), CALC_DATE);

        assertThat(result.lines())
                .extracting(EmployerLine::code, EmployerLine::amount)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("FSZN_EMPLOYER", money("0.00"))
                );
        assertThat(result.totalContributions()).isEqualTo(money("0.00"));
        assertThat(result.employerCost()).isEqualTo(money("0.00"));
    }
}
