package com.example.backend.payroll.base;

import com.example.backend.payroll.domain.base.model.SalesSummary;
import com.example.backend.payroll.domain.base.model.WorkVolumeSummary;
import com.example.backend.payroll.domain.shared.model.HoursSummary;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Period;

import java.math.BigDecimal;
import java.util.Currency;

public final class PayrollFixtures {

    private PayrollFixtures() {
    }

    public static Money money(String amount) {
        return Money.of(new BigDecimal(amount), Currency.getInstance("BYN"));
    }

    public static Period period() {
        return Period.of(2024, 5);
    }

    public static HoursSummary regularHours(String regular) {
        return new HoursSummary(
                new BigDecimal(regular),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    public static WorkVolumeSummary volume(String units) {
        return WorkVolumeSummary.of(1L, period(), new BigDecimal(units));
    }

    public static SalesSummary sales(String amount) {
        return SalesSummary.of(1L, period(), money(amount));
    }
}
