package com.example.backend.payroll.deduction;

import com.example.backend.payroll.domain.deduction.exception.InvalidDeductionException;
import com.example.backend.payroll.domain.deduction.model.Base;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.model.DeductionType;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeductionTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final Long EMPLOYEE_ID = 15L;
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 12, 31);
    private static final DateRange VALID_PERIOD = DateRange.of(START, END);

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @Nested
    class Creation {

        @Test
        void createsPercentageDeduction() {
            Percentage rate = Percentage.of(new BigDecimal("25"));

            Deduction deduction = Deduction.create(
                    EMPLOYEE_ID,
                    DeductionType.ALIMONY,
                    "Alimony 25%",
                    rate,
                    null,
                    Base.GROSS_MINUS_TAX,
                    VALID_PERIOD
            );

            assertThat(deduction.getId()).isNull();
            assertThat(deduction.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
            assertThat(deduction.getType()).isEqualTo(DeductionType.ALIMONY);
            assertThat(deduction.getDescription()).isEqualTo("Alimony 25%");
            assertThat(deduction.getPercentage()).isEqualTo(rate);
            assertThat(deduction.getFixedAmount()).isNull();
            assertThat(deduction.getBase()).isEqualTo(Base.GROSS_MINUS_TAX);
            assertThat(deduction.getValidPeriod()).isEqualTo(VALID_PERIOD);
        }

        @Test
        void createsFixedAmountDeduction() {
            Money fixed = money("150.00");

            Deduction deduction = Deduction.create(
                    EMPLOYEE_ID,
                    DeductionType.INSURANCE,
                    "Voluntary medical insurance",
                    null,
                    fixed,
                    null,
                    VALID_PERIOD
            );

            assertThat(deduction.getFixedAmount()).isEqualTo(fixed);
            assertThat(deduction.getPercentage()).isNull();
            assertThat(deduction.getBase()).isNull();
        }

        @Test
        void rehydratesDeduction() {
            Money fixed = money("50.00");

            Deduction deduction = Deduction.rehydrate(
                    99L,
                    EMPLOYEE_ID,
                    DeductionType.UNION,
                    "Trade union fee",
                    null,
                    fixed,
                    null,
                    VALID_PERIOD
            );

            assertThat(deduction.getId()).isEqualTo(99L);
            assertThat(deduction.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
            assertThat(deduction.getType()).isEqualTo(DeductionType.UNION);
            assertThat(deduction.getFixedAmount()).isEqualTo(fixed);
        }

        @Test
        void rejectsMissingMandatoryFields() {
            Money fixed = money("100.00");

            assertThatThrownBy(() -> Deduction.create(
                    null, DeductionType.COURT, "desc", null, fixed, null, VALID_PERIOD
            )).isInstanceOf(InvalidDeductionException.class);

            assertThatThrownBy(() -> Deduction.create(
                    EMPLOYEE_ID, null, "desc", null, fixed, null, VALID_PERIOD
            )).isInstanceOf(InvalidDeductionException.class);

            assertThatThrownBy(() -> Deduction.create(
                    EMPLOYEE_ID, DeductionType.COURT, "desc", null, fixed, null, null
            )).isInstanceOf(InvalidDeductionException.class);
        }

        @Test
        void rejectsWhenNeitherAmountNorPercentageProvided() {
            assertThatThrownBy(() -> Deduction.create(
                    EMPLOYEE_ID, DeductionType.CHARITY, "desc", null, null, Base.GROSS, VALID_PERIOD
            )).isInstanceOf(InvalidDeductionException.class);
        }

        @Test
        void rejectsWhenBothPercentageAndFixedAmountProvided() {
            assertThatThrownBy(() -> Deduction.create(
                    EMPLOYEE_ID,
                    DeductionType.COURT,
                    "desc",
                    Percentage.of(new BigDecimal("10")),
                    money("100.00"),
                    Base.GROSS,
                    VALID_PERIOD
            )).isInstanceOf(InvalidDeductionException.class);
        }

        @Test
        void rejectsPercentageWithoutBase() {
            assertThatThrownBy(() -> Deduction.create(
                    EMPLOYEE_ID,
                    DeductionType.UNION,
                    "desc",
                    Percentage.of(new BigDecimal("1")),
                    null,
                    null,
                    VALID_PERIOD
            )).isInstanceOf(InvalidDeductionException.class);
        }
    }

    @Nested
    class Applicability {

        @Test
        void appliesToDatesWithinRange() {
            Deduction deduction = Deduction.create(
                    EMPLOYEE_ID,
                    DeductionType.UNION,
                    "Union",
                    Percentage.of(new BigDecimal("1")),
                    null,
                    Base.GROSS,
                    VALID_PERIOD
            );

            assertThat(deduction.appliesTo(START)).isTrue();
            assertThat(deduction.appliesTo(LocalDate.of(2026, 6, 15))).isTrue();
            assertThat(deduction.appliesTo(END)).isTrue();
            assertThat(deduction.appliesTo(START.minusDays(1))).isFalse();
            assertThat(deduction.appliesTo(END.plusDays(1))).isFalse();
        }
    }
}
