package com.example.backend.payroll.domain.net.service;

import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.net.exception.InvalidNetException;
import com.example.backend.payroll.domain.shared.model.Money;

import java.math.BigDecimal;
import java.util.List;

public class NetCalculator {
    private static final BigDecimal MAX_DEDUCTION_PERCENT = BigDecimal.valueOf(50);

    public Money calculate(Money gross, Money tax, Money socialEmployee, List<DeductionResult> deductions) {
        if (gross == null) {
            throw new InvalidNetException("gross is required");
        }
        if (tax == null) {
            throw new InvalidNetException("tax is required");
        }
        if (socialEmployee == null) {
            throw new InvalidNetException("socialEmployee is required");
        }
        if (deductions == null) {
            deductions = List.of();
        }

        Money deductionsTotal = deductions.stream()
                .map(DeductionResult::amount)
                .reduce(Money.zero(gross.currency()), Money::add);

        Money totalWithholding = tax.add(socialEmployee).add(deductionsTotal);

        Money maxWithholding = gross.multiply(MAX_DEDUCTION_PERCENT.divide(BigDecimal.valueOf(100)));
        if (totalWithholding.isGreaterThan(maxWithholding)) {
            throw new InvalidNetException("total withholding exceeds 50% of gross");
        }

        Money net = gross.subtract(totalWithholding);

        if (net.isNegative()) {
            throw new InvalidNetException("net cannot be negative");
        }
        if (net.isGreaterThan(gross)) {
            throw new InvalidNetException("net cannot exceed gross");
        }
        return net;
    }
}
