package com.example.backend.payroll.domain.base.model;

import com.example.backend.payroll.domain.base.exception.InvalidSalesSummaryException;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Period;

import java.math.BigDecimal;
import java.util.Currency;

public record SalesSummary(
        Long employeeId,
        Period period,
        Money amount
) {
    public SalesSummary {
        if (employeeId == null) {
            throw new InvalidSalesSummaryException("employeeId is required");
        }
        if (period == null) {
            throw new InvalidSalesSummaryException("period is required");
        }
        if (amount == null) {
            throw new InvalidSalesSummaryException("amount is required");
        }
        if (amount.isNegative()) {
            throw new InvalidSalesSummaryException("amount must be >= 0");
        }
    }

    public static SalesSummary of(Long employeeId, Period period, Money amount) {
        return new SalesSummary(employeeId, period, amount);
    }

    public static SalesSummary of(Long employeeId, Period period, BigDecimal amount, Currency currency) {
        return new SalesSummary(employeeId, period, Money.of(amount, currency));
    }

    public boolean isEmpty() {
        return amount.isZero();
    }
}
