package com.example.backend.payroll.domain.accrual.policy;

import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.shared.model.Money;

public class BonusPolicy {
    public Money calculate(Money base, Money grossWithoutBonus, BonusRule rule) {
        if (rule == null) {
            return Money.zero(base.currency());
        }
        return switch (rule.getType()) {
            case PERCENT_GROSS -> grossWithoutBonus.multiply(rule.getPercentage());
            case PERCENT_SALARY -> base.multiply(rule.getPercentage());
            case FIXED -> rule.getFixedAmount();
        };
    }
}
