package com.example.backend.payroll.service;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

final class PayrollMocks {
    private PayrollMocks() {
    }

    static Money money(String value) {
        return Money.of(new BigDecimal(value), Currency.getInstance("BYN"));
    }

    static DateRange period2024() {
        return new DateRange(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
    }

    static DateRange period2025() {
        return new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
    }
}
