package com.example.backend.payroll.deduction;

import com.example.backend.payroll.domain.deduction.model.Base;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.deduction.model.DeductionType;
import com.example.backend.payroll.domain.deduction.policy.DeductionPolicy;
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

class DeductionPolicyTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final Long EMPLOYEE_ID = 1L;
    private static final LocalDate CALC_DATE = LocalDate.of(2026, 10, 31);
    private static final DateRange ACTIVE_RANGE = DateRange.of(
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31)
    );

    private DeductionPolicy policy;

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @BeforeEach
    void setUp() {
        policy = new DeductionPolicy();
    }

    @Test
    void rejectsNullGross() {
        assertThatThrownBy(() -> policy.calculate(null, money("100.00"), List.of(), CALC_DATE))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void returnsEmptyWhenNoDeductions() {
        assertThat(policy.calculate(money("2000.00"), money("260.00"), null, CALC_DATE)).isEmpty();
        assertThat(policy.calculate(money("2000.00"), money("260.00"), List.of(), CALC_DATE)).isEmpty();
    }

    @Test
    void calculatesPercentageFromGross() {
        Deduction unionFee = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.UNION,
                "Union 1%",
                Percentage.of(new BigDecimal("1")),
                null,
                Base.GROSS,
                ACTIVE_RANGE
        );

        List<DeductionResult> results = policy.calculate(
                money("2500.00"),
                money("325.00"),
                List.of(unionFee),
                CALC_DATE
        );

        assertThat(results).containsExactly(
                new DeductionResult("UNION", "Union 1%", money("25.00"))
        );
    }

    @Test
    void calculatesPercentageFromGrossMinusTax() {
        Deduction alimony = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.ALIMONY,
                "Alimony 25%",
                Percentage.of(new BigDecimal("25")),
                null,
                Base.GROSS_MINUS_TAX,
                ACTIVE_RANGE
        );

        List<DeductionResult> results = policy.calculate(
                money("2500.00"),
                money("325.00"),
                List.of(alimony),
                CALC_DATE
        );

        assertThat(results).containsExactly(
                new DeductionResult("ALIMONY", "Alimony 25%", money("543.75"))
        );
    }

    @Test
    void appliesFixedAmountDeduction() {
        Deduction insurance = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.INSURANCE,
                "Medical insurance",
                null,
                money("120.00"),
                null,
                ACTIVE_RANGE
        );

        List<DeductionResult> results = policy.calculate(
                money("2500.00"),
                money("325.00"),
                List.of(insurance),
                CALC_DATE
        );

        assertThat(results).containsExactly(
                new DeductionResult("INSURANCE", "Medical insurance", money("120.00"))
        );
    }

    @Test
    void filtersInactiveDeductionsAndPreservesOrder() {
        Deduction expired = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.CHARITY,
                "Expired charity",
                null,
                money("50.00"),
                null,
                DateRange.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 9, 30))
        );
        Deduction future = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.COURT,
                "Future court order",
                Percentage.of(new BigDecimal("10")),
                null,
                Base.GROSS,
                DateRange.of(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 12, 31))
        );
        Deduction activeFirst = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.ALIMONY,
                "Alimony",
                Percentage.of(new BigDecimal("25")),
                null,
                Base.GROSS_MINUS_TAX,
                ACTIVE_RANGE
        );
        Deduction activeSecond = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.UNION,
                "Union",
                Percentage.of(new BigDecimal("1")),
                null,
                Base.GROSS,
                ACTIVE_RANGE
        );

        List<DeductionResult> results = policy.calculate(
                money("2000.00"),
                money("260.00"),
                List.of(expired, activeFirst, future, activeSecond),
                CALC_DATE
        );

        assertThat(results).containsExactly(
                new DeductionResult("ALIMONY", "Alimony", money("435.00")),
                new DeductionResult("UNION", "Union", money("20.00"))
        );
    }

    @Test
    void roundsPercentageDeductionsProperly() {
        Deduction courtOrder = Deduction.create(
                EMPLOYEE_ID,
                DeductionType.COURT,
                "Court 33.33%",
                Percentage.of(new BigDecimal("33.33")),
                null,
                Base.GROSS_MINUS_TAX,
                ACTIVE_RANGE
        );

        List<DeductionResult> results = policy.calculate(
                money("1000.00"),
                money("130.00"),
                List.of(courtOrder),
                CALC_DATE
        );

        assertThat(results).containsExactly(
                new DeductionResult("COURT", "Court 33.33%", money("289.97"))
        );
    }
}
