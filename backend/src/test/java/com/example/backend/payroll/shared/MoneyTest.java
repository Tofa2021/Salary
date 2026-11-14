package com.example.backend.payroll.shared;

import com.example.backend.payroll.domain.shared.exception.InvalidMoneyException;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MoneyTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final Currency BYN = Currency.getInstance("BYN");

    @Nested
    class Creation {

        @Test
        void createsFromBigDecimalAndNormalizesScale() {
            Money whole = Money.of(new BigDecimal("100"), USD);
            Money oneDecimal = Money.of(new BigDecimal("100.5"), USD);
            Money twoDecimals = Money.of(new BigDecimal("100.55"), USD);

            assertThat(whole.amount()).isEqualByComparingTo("100.00");
            assertThat(whole.amount().scale()).isEqualTo(2);
            assertThat(oneDecimal.amount()).isEqualByComparingTo("100.50");
            assertThat(twoDecimals.amount()).isEqualByComparingTo("100.55");
            assertThat(whole.currency()).isEqualTo(USD);
        }

        @Test
        void createsFromDouble() {
            Money money = Money.of(12.3, EUR);

            assertThat(money.amount()).isEqualByComparingTo("12.30");
            assertThat(money.amount().scale()).isEqualTo(2);
            assertThat(money.currency()).isEqualTo(EUR);
        }

        @Test
        void createsZero() {
            Money zero = Money.zero(BYN);

            assertThat(zero.amount()).isEqualByComparingTo("0.00");
            assertThat(zero.amount().scale()).isEqualTo(2);
            assertThat(zero.currency()).isEqualTo(BYN);
            assertThat(zero.isZero()).isTrue();
        }

        @Test
        void allowsNegativeAmounts() {
            Money negative = Money.of(new BigDecimal("-50.25"), USD);

            assertThat(negative.amount()).isEqualByComparingTo("-50.25");
            assertThat(negative.isNegative()).isTrue();
        }

        @Test
        void rejectsNullAmount() {
            assertThatThrownBy(() -> Money.of(null, USD))
                    .isInstanceOf(InvalidMoneyException.class);
        }

        @Test
        void rejectsNullCurrency() {
            assertThatThrownBy(() -> Money.of(BigDecimal.TEN, null))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> Money.zero(null))
                    .isInstanceOf(InvalidMoneyException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"10.123", "0.001", "-5.999", "1.000"})
        void rejectsScaleGreaterThanTwo(String rawAmount) {
            BigDecimal amount = new BigDecimal(rawAmount);

            assertThatThrownBy(() -> Money.of(amount, USD))
                    .isInstanceOf(InvalidMoneyException.class);
        }

        @Test
        void rejectsDoubleWithMoreThanTwoDecimals() {
            assertThatThrownBy(() -> Money.of(10.125, USD))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }

    @Nested
    class Equality {

        @Test
        void equalsByAmountAndCurrency() {
            Money first = Money.of(new BigDecimal("10"), USD);
            Money second = Money.of(new BigDecimal("10.00"), USD);

            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void notEqualForDifferentCurrencies() {
            Money usd = Money.of(new BigDecimal("10.00"), USD);
            Money eur = Money.of(new BigDecimal("10.00"), EUR);

            assertThat(usd).isNotEqualTo(eur);
        }
    }

    @Nested
    class AdditionAndSubtraction {

        @Test
        void addsSameCurrency() {
            Money a = Money.of(new BigDecimal("10.25"), USD);
            Money b = Money.of(new BigDecimal("5.75"), USD);

            assertThat(a.add(b)).isEqualTo(Money.of(new BigDecimal("16.00"), USD));
        }

        @Test
        void addsNegativeAndPositive() {
            Money debt = Money.of(new BigDecimal("-20.00"), USD);
            Money payment = Money.of(new BigDecimal("35.50"), USD);

            assertThat(debt.add(payment)).isEqualTo(Money.of(new BigDecimal("15.50"), USD));
        }

        @Test
        void subtractsSameCurrency() {
            Money balance = Money.of(new BigDecimal("10.00"), USD);
            Money expense = Money.of(new BigDecimal("15.40"), USD);

            Money result = balance.subtract(expense);

            assertThat(result).isEqualTo(Money.of(new BigDecimal("-5.40"), USD));
            assertThat(result.isNegative()).isTrue();
        }

        @Test
        void rejectsNullInAddAndSubtract() {
            Money money = Money.of(new BigDecimal("10.00"), USD);

            assertThatThrownBy(() -> money.add(null))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> money.subtract(null))
                    .isInstanceOf(InvalidMoneyException.class);
        }

        @Test
        void rejectsCurrencyMismatch() {
            Money usd = Money.of(new BigDecimal("10.00"), USD);
            Money eur = Money.of(new BigDecimal("10.00"), EUR);

            assertThatThrownBy(() -> usd.add(eur))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> usd.subtract(eur))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }

    @Nested
    class Multiplication {

        @ParameterizedTest
        @CsvSource({
                "100.00, 0.15,   15.00",
                "10.00,  0.333,  3.33",
                "10.00,  0.335,  3.35",
                "10.05,  0.5,    5.03",
                "100.00, 0,      0.00",
                "100.00, -0.10, -10.00",
                "-10.05, 0.5,   -5.03"
        })
        void multipliesByFactorWithRounding(String amount, String factor, String expected) {
            Money money = Money.of(new BigDecimal(amount), USD);

            Money result = money.multiply(new BigDecimal(factor));

            assertThat(result).isEqualTo(Money.of(new BigDecimal(expected), USD));
        }

        @ParameterizedTest
        @CsvSource({
                "200.00, 13, 26.00",
                "100.00, 33.33, 33.33",
                "10.00,  0.5, 0.05",
                "10.05,  50, 5.03"
        })
        void multipliesByPercentage(String amount, String percentValue, String expected) {
            Money money = Money.of(new BigDecimal(amount), USD);
            Percentage percentage = Percentage.of(new BigDecimal(percentValue));

            Money result = money.multiply(percentage);

            assertThat(result).isEqualTo(Money.of(new BigDecimal(expected), USD));
        }
    }

    @Nested
    class Division {

        @ParameterizedTest
        @CsvSource({
                "100.00, 4,    25.00",
                "100.00, 3,    33.33",
                "200.00, 3,    66.67",
                "10.05,  2,    5.03",
                "-10.05, 2,   -5.03",
                "0.00,   7,    0.00"
        })
        void dividesByIntWithRounding(String amount, int divisor, String expected) {
            Money money = Money.of(new BigDecimal(amount), USD);

            Money result = money.divide(divisor);

            assertThat(result).isEqualTo(Money.of(new BigDecimal(expected), USD));
        }

        @ParameterizedTest
        @CsvSource({
                "100.00, 2.5,   40.00",
                "100.00, 3,     33.33",
                "1.00,   8,     0.13",
                "10.00,  0.3,   33.33"
        })
        void dividesByBigDecimalWithRounding(String amount, String divisor, String expected) {
            Money money = Money.of(new BigDecimal(amount), USD);

            Money result = money.divide(new BigDecimal(divisor));

            assertThat(result).isEqualTo(Money.of(new BigDecimal(expected), USD));
        }

        @Test
        void rejectsNullDivisor() {
            Money money = Money.of(new BigDecimal("100.00"), USD);
            assertThatThrownBy(() -> money.divide(null))
                    .isInstanceOf(InvalidMoneyException.class);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -100})
        void rejectsNonPositiveIntDivisor(int invalidDivisor) {
            Money money = Money.of(new BigDecimal("100.00"), USD);
            assertThatThrownBy(() -> money.divide(invalidDivisor))
                    .isInstanceOf(InvalidMoneyException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "0.00", "-0.01", "-10"})
        void rejectsNonPositiveBigDecimalDivisor(String invalidDivisor) {
            Money money = Money.of(new BigDecimal("100.00"), USD);
            assertThatThrownBy(() -> money.divide(new BigDecimal(invalidDivisor)))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }

    @Nested
    class Ratio {
        @Test
        void calculatesRatioBetweenSameCurrencies() {
            Money part = Money.of(new BigDecimal("1.00"), USD);
            Money total = Money.of(new BigDecimal("3.00"), USD);
            BigDecimal ratio = part.ratioTo(total);
            assertThat(ratio).isEqualByComparingTo("0.333333");
        }

        @Test
        void calculatesRatioGreaterThanOne() {
            Money larger = Money.of(new BigDecimal("150.00"), USD);
            Money base = Money.of(new BigDecimal("100.00"), USD);
            assertThat(larger.ratioTo(base)).isEqualByComparingTo("1.500000");
        }

        @Test
        void returnsZeroWhenNumeratorIsZero() {
            Money zero = Money.zero(USD);
            Money total = Money.of(new BigDecimal("50.00"), USD);
            assertThat(zero.ratioTo(total)).isEqualByComparingTo("0.000000");
        }

        @Test
        void rejectsZeroDenominatorInRatio() {
            Money amount = Money.of(new BigDecimal("50.00"), USD);
            Money zero = Money.zero(USD);
            assertThatThrownBy(() -> amount.ratioTo(zero))
                    .isInstanceOf(InvalidMoneyException.class);
        }

        @Test
        void rejectsNullOrDifferentCurrencyInRatio() {
            Money usd = Money.of(new BigDecimal("50.00"), USD);
            Money eur = Money.of(new BigDecimal("50.00"), EUR);
            assertThatThrownBy(() -> usd.ratioTo(null))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> usd.ratioTo(eur))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }

    @Nested
    class Comparisons {
        @Test
        void checksSignPredicates() {
            Money zero = Money.zero(USD);
            Money positive = Money.of(new BigDecimal("0.01"), USD);
            Money negative = Money.of(new BigDecimal("-0.01"), USD);
            assertThat(zero.isZero()).isTrue();
            assertThat(zero.isNegative()).isFalse();
            assertThat(positive.isZero()).isFalse();
            assertThat(positive.isNegative()).isFalse();
            assertThat(negative.isZero()).isFalse();
            assertThat(negative.isNegative()).isTrue();
        }

        @Test
        void comparesAmounts() {
            Money small = Money.of(new BigDecimal("10.00"), USD);
            Money large = Money.of(new BigDecimal("10.01"), USD);
            Money equalToSmall = Money.of(new BigDecimal("10"), USD);
            assertThat(large.isGreaterThan(small)).isTrue();
            assertThat(small.isGreaterThan(large)).isFalse();
            assertThat(small.isGreaterThan(equalToSmall)).isFalse();
            assertThat(small.isLessThan(large)).isTrue();
            assertThat(large.isLessThan(small)).isFalse();
            assertThat(small.isLessThan(equalToSmall)).isFalse();
        }

        @Test
        void rejectsInvalidComparisons() {
            Money usd = Money.of(new BigDecimal("10.00"), USD);
            Money eur = Money.of(new BigDecimal("20.00"), EUR);
            assertThatThrownBy(() -> usd.isGreaterThan(null))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> usd.isLessThan(null))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> usd.isGreaterThan(eur))
                    .isInstanceOf(InvalidMoneyException.class);
            assertThatThrownBy(() -> usd.isLessThan(eur))
                    .isInstanceOf(InvalidMoneyException.class);
        }
    }
}
