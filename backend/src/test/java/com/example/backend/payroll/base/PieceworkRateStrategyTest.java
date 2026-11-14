package com.example.backend.payroll.base;

import com.example.backend.payroll.domain.base.exception.InvalidRateException;
import com.example.backend.payroll.domain.base.model.WorkVolumeSummary;
import com.example.backend.payroll.domain.base.rate.PieceworkRateStrategy;
import com.example.backend.payroll.domain.base.rate.RateCalculationContext;
import com.example.backend.payroll.domain.shared.model.RateType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static com.example.backend.payroll.base.PayrollFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PieceworkRateStrategyTest {
    private final PieceworkRateStrategy strategy = new PieceworkRateStrategy();

    @Test
    void supports_piecework_rate_type() {
        assertThat(strategy.supports()).isEqualTo(RateType.PIECEWORK);
    }

    @Nested
    class BaseAmount {

        @ParameterizedTest(name = "{0} BYN/ед × {1} ед = {2}")
        @CsvSource({
                "50,  0,     0",
                "50,  1,     50",
                "50,  100,   5000",
                "0,   100,   0",
                "50,  0.5,   25",
        })
        void multiplies_rate_by_units(String rate, String units, String expected) {
            var ctx = RateCalculationContext.ofVolume(volume(units));

            var result = strategy.baseAmount(money(rate), ctx);

            assertThat(result).isEqualTo(money(expected));
        }

        @Test
        void rejects_null_volume() {
            var ctx = RateCalculationContext.ofHours(regularHours("8"));

            assertThatThrownBy(() -> strategy.baseAmount(money("50"), ctx))
                    .isInstanceOf(InvalidRateException.class);
        }

        @Test
        void rejects_negative_units() {
            assertThatThrownBy(() ->
                    strategy.baseAmount(money("50"),
                            RateCalculationContext.ofVolume(
                                    WorkVolumeSummary.of(1L, period(), new BigDecimal("-1")))))
                    .isInstanceOf(RuntimeException.class); // InvalidWorkVolumeException при создании
        }
    }

    @Nested
    class HourlyRate {

        @Test
        void throws_for_hourly_rate_because_not_supported() {
            assertThatThrownBy(() -> strategy.hourlyRate(money("50"), new BigDecimal("168")))
                    .isInstanceOf(InvalidRateException.class)
                    .hasMessageContaining("PIECEWORK");
        }
    }
}
