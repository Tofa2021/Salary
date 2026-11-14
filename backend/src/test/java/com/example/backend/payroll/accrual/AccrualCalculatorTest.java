package com.example.backend.payroll.accrual;

import com.example.backend.payroll.domain.accrual.model.AccrualLine;
import com.example.backend.payroll.domain.accrual.model.AccrualResult;
import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.model.BonusType;
import com.example.backend.payroll.domain.accrual.policy.BonusPolicy;
import com.example.backend.payroll.domain.accrual.policy.HolidayPolicy;
import com.example.backend.payroll.domain.accrual.policy.NightShiftPolicy;
import com.example.backend.payroll.domain.accrual.policy.OvertimePolicy;
import com.example.backend.payroll.domain.accrual.service.AccrualCalculator;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.HoursSummary;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AccrualCalculatorTest {
    private final OvertimePolicy overtimePolicy = new OvertimePolicy(new BigDecimal("1.5"), new BigDecimal("2.0"));
    private final NightShiftPolicy nightPolicy = new NightShiftPolicy(Percentage.of(20));
    private final HolidayPolicy holidayPolicy = new HolidayPolicy(new BigDecimal("2.0"));
    private final BonusPolicy bonusPolicy = new BonusPolicy();
    private final AccrualCalculator calculator = new AccrualCalculator(overtimePolicy, nightPolicy, holidayPolicy, bonusPolicy);

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("BYN"));
    }

    private static HoursSummary hours(String regular, String overtime, String night, String holiday) {
        return new HoursSummary(
                new BigDecimal(regular),
                new BigDecimal(overtime),
                new BigDecimal(night),
                new BigDecimal(holiday)
        );
    }

    private static DateRange anyPeriod() {
        return new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31));
    }

    private static BonusRule percentGross(int p) {
        return BonusRule.create(null, 1L, BonusType.PERCENT_GROSS,
                Percentage.of(BigDecimal.valueOf(p)), null, anyPeriod());
    }

    private static BonusRule fixed(String amount) {
        return BonusRule.create(null, 1L, BonusType.FIXED,
                null, money(amount), anyPeriod());
    }

    private static List<String> codes(AccrualResult result) {
        return result.lines().stream().map(AccrualLine::code).toList();
    }

    @Nested
    class LineComposition {

        @Test
        void no_hours_and_no_bonus_yields_empty_lines() {
            AccrualResult result = calculator.calculate(
                    money("1000"), money("100"),
                    hours("0", "0", "0", "0"), null);

            assertThat(result.lines()).isEmpty();
            assertThat(result.total()).isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }

        @Test
        void adds_line_only_for_non_zero_accrual() {
            AccrualResult result = calculator.calculate(
                    money("1000"), money("100"),
                    hours("0", "0", "10", "0"), null);

            assertThat(codes(result)).containsExactly("NIGHT");
            assertThat(result.total()).isEqualTo(money("1200"));
        }

        @Test
        void adds_lines_in_deterministic_order_overtime_night_holiday_bonus() {
            AccrualResult result = calculator.calculate(
                    money("1000"), money("100"),
                    hours("0", "2", "1", "1"), percentGross(10));

            assertThat(codes(result)).containsExactly("OVERTIME", "NIGHT", "HOLIDAY", "BONUS");
        }
    }

    @Nested
    class Totals {

        @Test
        void total_sums_only_accrual_lines_not_base() {
            AccrualResult result = calculator.calculate(
                    money("1000"), money("100"),
                    hours("0", "2", "1", "1"), percentGross(10));

            assertThat(result.total()).isEqualTo(money("782"));
        }

        @Test
        void percent_gross_bonus_is_calculated_from_base_plus_accruals() {
            AccrualResult result = calculator.calculate(
                    money("1000"), money("100"),
                    hours("0", "2", "0", "0"), percentGross(10));

            assertThat(result.lines()).extracting(AccrualLine::amount)
                    .containsExactly(money("300"), money("130"));
            assertThat(result.total()).isEqualTo(money("430"));
        }

        @Test
        void fixed_bonus_is_added_after_accruals() {
            AccrualResult result = calculator.calculate(
                    money("1000"), money("100"),
                    hours("0", "0", "0", "0"), fixed("500"));

            assertThat(codes(result)).containsExactly("BONUS");
            assertThat(result.total()).isEqualTo(money("500"));
        }
    }
}
