package com.example.backend.payroll.api.dto.deduction;

import com.example.backend.payroll.domain.deduction.model.Base;
import com.example.backend.payroll.domain.deduction.model.DeductionType;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;

public record ChangeDeductionValueCommand(
        DeductionType type,
        String description,
        Percentage percentage,
        Money fixedAmount,
        Base base
) {
}
