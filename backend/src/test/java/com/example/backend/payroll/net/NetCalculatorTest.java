package com.example.backend.payroll.net;

import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.net.exception.InvalidNetException;
import com.example.backend.payroll.domain.net.service.NetCalculator;
import com.example.backend.payroll.domain.shared.exception.InvalidMoneyException;
import com.example.backend.payroll.domain.shared.model.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NetCalculatorTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final Currency USD = Currency.getInstance("USD");

    private NetCalculator calculator;

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    private static DeductionResult deduction(String code, String amount) {
        return new DeductionResult(code, "Description for " + code, money(amount));
    }

    @BeforeEach
    void setUp() {
        calculator = new NetCalculator();
    }

    @Nested
    class StandardCalculation {

        @Test
        void calculatesNetWithAllWithholdings() {
            Money gross = money("2500.00");
            Money tax = money("325.00");
            Money socialEmployee = money("25.00");
            List<DeductionResult> deductions = List.of(
                    deduction("ALIMONY", "543.75"),
                    deduction("UNION", "25.00")
            );

            Money net = calculator.calculate(gross, tax, socialEmployee, deductions);

            assertThat(net).isEqualTo(money("1581.25"));
        }

        @Test
        void handlesNullOrEmptyDeductions() {
            Money gross = money("2000.00");
            Money tax = money("260.00");
            Money socialEmployee = money("20.00");

            assertThat(calculator.calculate(gross, tax, socialEmployee, null))
                    .isEqualTo(money("1720.00"));
            assertThat(calculator.calculate(gross, tax, socialEmployee, List.of()))
                    .isEqualTo(money("1720.00"));
        }

        @Test
        void returnsGrossWhenNoWithholdings() {
            Money gross = money("1500.00");

            Money net = calculator.calculate(gross, money("0.00"), money("0.00"), List.of());

            assertThat(net).isEqualTo(money("1500.00"));
        }

        @Test
        void returnsZeroWhenGrossIsZero() {
            Money zero = money("0.00");

            Money net = calculator.calculate(zero, zero, zero, List.of());

            assertThat(net).isEqualTo(zero);
            assertThat(net.isZero()).isTrue();
        }
    }

    @Nested
    class WithholdingLimit {

        @Test
        void allowsExactlyFiftyPercentWithholding() {
            Money gross = money("2000.00");
            Money tax = money("260.00");
            Money socialEmployee = money("20.00");
            List<DeductionResult> deductions = List.of(deduction("ALIMONY", "720.00"));

            Money net = calculator.calculate(gross, tax, socialEmployee, deductions);

            assertThat(net).isEqualTo(money("1000.00"));
        }

        @Test
        void handlesHalfUpRoundingOnFiftyPercentBoundary() {
            Money gross = money("100.01");
            Money tax = money("13.00");
            Money socialEmployee = money("1.00");
            List<DeductionResult> deductions = List.of(deduction("COURT", "36.01"));

            Money net = calculator.calculate(gross, tax, socialEmployee, deductions);
            assertThat(net).isEqualTo(money("50.00"));

            List<DeductionResult> exceedingDeductions = List.of(deduction("COURT", "36.02"));
            assertThatThrownBy(() -> calculator.calculate(gross, tax, socialEmployee, exceedingDeductions))
                    .isInstanceOf(InvalidNetException.class);
        }

        @Test
        void rejectsWithholdingExceedingFiftyPercent() {
            Money gross = money("2000.00");
            Money tax = money("260.00");
            Money socialEmployee = money("20.00");
            List<DeductionResult> deductions = List.of(deduction("ALIMONY", "720.01"));

            assertThatThrownBy(() -> calculator.calculate(gross, tax, socialEmployee, deductions))
                    .isInstanceOf(InvalidNetException.class);
        }

        @Test
        void rejectsWhenStatutoryWithholdingsAloneExceedFiftyPercent() {
            Money gross = money("1000.00");
            Money tax = money("450.00");
            Money socialEmployee = money("50.01");

            assertThatThrownBy(() -> calculator.calculate(gross, tax, socialEmployee, List.of()))
                    .isInstanceOf(InvalidNetException.class);
        }
    }

    @Nested
    class Validation {

        @Test
        void rejectsNullRequiredArguments() {
            Money valid = money("100.00");

            assertThatThrownBy(() -> calculator.calculate(null, valid, valid, List.of()))
                    .isInstanceOf(InvalidNetException.class);
            assertThatThrownBy(() -> calculator.calculate(valid, null, valid, List.of()))
                    .isInstanceOf(InvalidNetException.class);
            assertThatThrownBy(() -> calculator.calculate(valid, valid, null, List.of()))
                    .isInstanceOf(InvalidNetException.class);
        }

        @Test
        void rejectsNetExceedingGross() {
            Money gross = money("1000.00");
            Money negativeTax = money("-50.00");
            Money socialEmployee = money("0.00");

            assertThatThrownBy(() -> calculator.calculate(gross, negativeTax, socialEmployee, List.of()))
                    .isInstanceOf(InvalidNetException.class);
        }

        @Test
        void rejectsCurrencyMismatch() {
            Money grossByn = money("1000.00");
            Money taxByn = money("130.00");
            Money socialByn = money("10.00");
            Money taxUsd = Money.of(new BigDecimal("130.00"), USD);
            DeductionResult deductionUsd = new DeductionResult(
                    "UNION",
                    "Union USD",
                    Money.of(new BigDecimal("10.00"), USD)
            );

            assertThatThrownBy(() -> calculator.calculate(grossByn, taxUsd, socialByn, List.of()))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> calculator.calculate(grossByn, taxByn, socialByn, List.of(deductionUsd)))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }
}
