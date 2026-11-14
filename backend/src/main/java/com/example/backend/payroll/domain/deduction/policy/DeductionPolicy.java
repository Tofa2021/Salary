package com.example.backend.payroll.domain.deduction.policy;

import com.example.backend.payroll.domain.deduction.model.Base;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.shared.model.Money;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeductionPolicy {
    public List<DeductionResult> calculate(Money gross, Money tax, List<Deduction> deductions, LocalDate date) {
        if (gross == null) {
            throw new IllegalArgumentException("gross is required");
        }
        if (deductions == null) {
            return List.of();
        }

        List<DeductionResult> result = new ArrayList<>();
        for (Deduction deduction : deductions) {
            if (!deduction.appliesTo(date)) continue;

            Money baseAmount = deduction.getBase() == Base.GROSS_MINUS_TAX
                    ? gross.subtract(tax)
                    : gross;

            Money amount = deduction.getPercentage() != null
                    ? baseAmount.multiply(deduction.getPercentage())
                    : deduction.getFixedAmount();

            result.add(new DeductionResult(deduction.getType().name(), deduction.getDescription(), amount));
        }
        return result;
    }
}
