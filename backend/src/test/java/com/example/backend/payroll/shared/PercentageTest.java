package com.example.backend.payroll.shared;

import com.example.backend.payroll.domain.shared.exception.InvalidPercentageException;
import com.example.backend.payroll.domain.shared.model.Percentage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PercentageTest {

    @Nested
    class Creation {

        @ParameterizedTest
        @ValueSource(strings = {"0", "0.00", "0.01", "13.5", "50", "99.99", "100", "100.00"})
        void createsFromValidBigDecimal(String rawValue) {
            BigDecimal input = new BigDecimal(rawValue);

            Percentage percentage = Percentage.of(input);

            assertThat(percentage.value()).isEqualByComparingTo(input);
        }

        @ParameterizedTest
        @ValueSource(doubles = {0.0, 0.5, 13.25, 100.0})
        void createsFromValidDouble(double rawValue) {
            Percentage percentage = Percentage.of(rawValue);

            assertThat(percentage.value()).isEqualByComparingTo(BigDecimal.valueOf(rawValue));
        }

        @Test
        void createsZero() {
            Percentage zero = Percentage.zero();

            assertThat(zero.value()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(zero).isEqualTo(Percentage.of(0));
        }

        @Test
        void rejectsNull() {
            assertThatThrownBy(() -> Percentage.of(null))
                    .isInstanceOf(InvalidPercentageException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"-0.0001", "-0.01", "-1", "-100"})
        void rejectsNegativeBigDecimal(String rawValue) {
            BigDecimal negative = new BigDecimal(rawValue);

            assertThatThrownBy(() -> Percentage.of(negative))
                    .isInstanceOf(InvalidPercentageException.class);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-0.01, -50.0})
        void rejectsNegativeDouble(double negative) {
            assertThatThrownBy(() -> Percentage.of(negative))
                    .isInstanceOf(InvalidPercentageException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"100.0001", "100.01", "101", "1000"})
        void rejectsGreaterThanHundredBigDecimal(String rawValue) {
            BigDecimal aboveMax = new BigDecimal(rawValue);

            assertThatThrownBy(() -> Percentage.of(aboveMax))
                    .isInstanceOf(InvalidPercentageException.class);
        }

        @ParameterizedTest
        @ValueSource(doubles = {100.01, 150.0})
        void rejectsGreaterThanHundredDouble(double aboveMax) {
            assertThatThrownBy(() -> Percentage.of(aboveMax))
                    .isInstanceOf(InvalidPercentageException.class);
        }
    }

    @Nested
    class FractionConversion {

        @ParameterizedTest
        @CsvSource({
                "0,       0.0000",
                "100,     1.0000",
                "50,      0.5000",
                "13,      0.1300",
                "12.5,    0.1250",
                "0.05,    0.0005",
                "0.005,   0.0001",
                "0.004,   0.0000",
                "33.33,   0.3333",
                "33.335,  0.3334",
                "33.334,  0.3333",
                "99.995,  1.0000"
        })
        void convertsToFractionWithHalfUpRounding(String percentValue, String expectedFraction) {
            Percentage percentage = Percentage.of(new BigDecimal(percentValue));

            BigDecimal fraction = percentage.asFraction();

            assertThat(fraction).isEqualByComparingTo(expectedFraction);
            assertThat(fraction.scale()).isEqualTo(4);
        }
    }

    @Nested
    class Equality {

        @Test
        void equalsForSameValue() {
            Percentage first = Percentage.of(new BigDecimal("15.5"));
            Percentage second = Percentage.of(15.5);

            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        void notEqualForDifferentValues() {
            Percentage first = Percentage.of(10);
            Percentage second = Percentage.of(20);

            assertThat(first).isNotEqualTo(second);
        }
    }
}
