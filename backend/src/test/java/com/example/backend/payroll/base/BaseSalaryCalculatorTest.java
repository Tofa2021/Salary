package com.example.backend.payroll.base;

import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.rate.RateCalculationStrategyRegistry;
import com.example.backend.payroll.domain.base.service.BaseSalaryCalculator;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.RateType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

import static com.example.backend.payroll.base.PayrollFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BaseSalaryCalculatorTest {

    private static final DateRange ANY_PERIOD =
            new DateRange(LocalDate.of(2020, 1, 1), LocalDate.of(2030, 12, 31));

    private final RateCalculationStrategyRegistry registry =
            RateCalculationStrategyRegistry.defaultRegistry();
    private final BaseSalaryCalculator calculator = new BaseSalaryCalculator(registry);

    private TariffRate rate(String amount, RateType type) {
        return TariffRate.create(1L, money(amount), type, ANY_PERIOD, registry.get(type));
    }

    @Nested
    class DelegationToStrategy {

        @ParameterizedTest(name = "{0} {1} × {2} ч = {3}")
        @CsvSource({
                "HOURLY,   100,   8,   800",
                "DAILY,    800,   8,   800",
                "MONTHLY,  16800, 168, 16800",
        })
        void produces_expected_amount_per_rate_type(String type, String amount, String hours, String expected) {
            var tariff = rate(amount, RateType.valueOf(type));

            var result = calculator.calculate(
                    tariff, regularHours(hours), null, null,
                    RateType.valueOf(type) == RateType.MONTHLY ? new BigDecimal("168") : null);

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void passes_volume_to_piecework_strategy() {
            var tariff = rate("50", RateType.PIECEWORK);

            var result = calculator.calculate(
                    tariff, regularHours("0"), volume("100"), null, null);

            assertThat(result).isEqualTo(money("5000"));
        }
    }

    @Nested
    class Contract {

        @Test
        void rejects_null_rate() {
            assertThatThrownBy(() -> calculator.calculate(
                    null, regularHours("8"), null, null, null))
                    .isInstanceOf(InvalidRateException.class);
        }

        @Test
        void rejects_null_hours() {
            assertThatThrownBy(() -> calculator.calculate(
                    rate("100", RateType.HOURLY), null, null, null, null))
                    .isInstanceOf(InvalidRateException.class);
        }
    }

    @Nested
    class Invariants {

        @Test
        void result_is_never_negative_for_valid_inputs() {
            for (RateType type : new RateType[]{RateType.HOURLY, RateType.DAILY}) {
                var tariff = rate("100", type);
                var result = calculator.calculate(
                        tariff, regularHours("8"), null, null, null);
                assertThat(result.isNegative())
                        .as("type=%s", type)
                        .isFalse();
            }
        }

        @Test
        void zero_hours_yields_zero() {
            for (RateType type : new RateType[]{RateType.HOURLY, RateType.DAILY, RateType.MONTHLY}) {
                var tariff = rate("1000", type);
                var result = calculator.calculate(
                        tariff, regularHours("0"), null, null,
                        type == RateType.MONTHLY ? new BigDecimal("168") : null);
                assertThat(result)
                        .as("type=%s", type)
                        .isEqualTo(com.example.backend.payroll.domain.shared.model.Money.zero(Currency.getInstance("BYN")));
            }
        }
    }
}
