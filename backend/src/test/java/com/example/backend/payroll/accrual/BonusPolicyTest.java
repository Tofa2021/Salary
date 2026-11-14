package com.example.backend.payroll.accrual;

import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.model.BonusType;
import com.example.backend.payroll.domain.accrual.policy.BonusPolicy;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

class BonusPolicyTest {
    private final BonusPolicy policy = new BonusPolicy();

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("BYN"));
    }

    private static DateRange anyPeriod() {
        return new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31));
    }

    private static BonusRule percentGross(int percent) {
        return BonusRule.create(null, 1L, BonusType.PERCENT_GROSS,
                Percentage.of(BigDecimal.valueOf(percent)), null, anyPeriod());
    }

    private static BonusRule percentSalary(int percent) {
        return BonusRule.create(null, 1L, BonusType.PERCENT_SALARY,
                Percentage.of(BigDecimal.valueOf(percent)), null, anyPeriod());
    }

    private static BonusRule fixed(String amount) {
        return BonusRule.create(null, 1L, BonusType.FIXED,
                null, money(amount), anyPeriod());
    }

    @Nested
    class NoRule {

        @Test
        void null_rule_yields_zero_in_base_currency() {
            Money result = policy.calculate(money("1000"), money("1500"), null);

            assertThat(result).isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }
    }

    @Nested
    class PercentGross {

        @Test
        void percent_of_gross_including_all_accruals() {
            Money result = policy.calculate(
                    money("1000"), money("1500"), percentGross(10));

            assertThat(result).isEqualTo(money("150"));
        }

        @Test
        void zero_percent_yields_zero() {
            Money result = policy.calculate(
                    money("1000"), money("1500"), percentGross(0));

            assertThat(result).isEqualTo(Money.zero(Currency.getInstance("BYN")));
        }
    }

    @Nested
    class PercentSalary {

        @Test
        void percent_of_base_salary_not_gross() {
            Money result = policy.calculate(
                    money("1000"), money("1500"), percentSalary(20));

            assertThat(result).isEqualTo(money("200"));
        }
    }

    @Nested
    class Fixed {

        @Test
        void ignores_base_and_gross() {
            Money result = policy.calculate(
                    money("1000"), money("1500"), fixed("300"));

            assertThat(result).isEqualTo(money("300"));
        }
    }

    @Nested
    class Invariants {

        @Test
        void result_is_never_negative() {
            for (BonusRule rule : new BonusRule[]{
                    percentGross(0), percentGross(50),
                    percentSalary(0), percentSalary(50),
                    fixed("0"), fixed("500")
            }) {
                Money result = policy.calculate(money("1000"), money("1500"), rule);
                assertThat(result.isNegative())
                        .as("rule type=%s", rule.getType())
                        .isFalse();
            }
        }
    }
}
