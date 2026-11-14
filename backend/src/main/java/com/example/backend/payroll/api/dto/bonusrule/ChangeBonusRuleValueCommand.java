package com.example.backend.payroll.api.dto.bonusrule;

import com.example.backend.payroll.domain.accrual.model.BonusType;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;

public record ChangeBonusRuleValueCommand(
        BonusType type,
        Percentage percentage,
        Money fixedAmount
) {
}
