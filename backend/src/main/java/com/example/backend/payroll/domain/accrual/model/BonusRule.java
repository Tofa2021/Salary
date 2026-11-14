package com.example.backend.payroll.domain.accrual.model;

import com.example.backend.payroll.domain.accrual.exception.InvalidBonusRuleException;
import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BonusRule {
    private Long id;
    private Long positionId;
    private Long employeeId;
    private BonusType type;
    private Percentage percentage;
    private Money fixedAmount;
    private DateRange validPeriod;

    private BonusRule() {
    }

    public static BonusRule create(
            Long positionId, Long employeeId, BonusType type,
            Percentage percentage, Money fixedAmount, DateRange validPeriod
    ) {
        if (type == null) {
            throw new InvalidBonusRuleException("type is required");
        }
        if (validPeriod == null) {
            throw new InvalidBonusRuleException("validPeriod is required");
        }
        if (positionId == null && employeeId == null) {
            throw new InvalidBonusRuleException("positionId or employeeId is required");
        }
        if (positionId != null && employeeId != null) {
            throw new InvalidBonusRuleException("only one of positionId/employeeId");
        }
        if (type == BonusType.FIXED && fixedAmount == null) {
            throw new InvalidBonusRuleException("fixedAmount required for FIXED");
        }
        if (type != BonusType.FIXED && percentage == null) {
            throw new InvalidBonusRuleException("percentage required for PERCENT_*");
        }

        BonusRule rule = new BonusRule();
        rule.positionId = positionId;
        rule.employeeId = employeeId;
        rule.type = type;
        rule.percentage = percentage;
        rule.fixedAmount = fixedAmount;
        rule.validPeriod = validPeriod;
        return rule;
    }

    public static BonusRule rehydrate(
            Long id, Long positionId, Long employeeId, BonusType type,
            Percentage percentage, Money fixedAmount, DateRange validPeriod
    ) {
        BonusRule rule = new BonusRule();
        rule.id = id;
        rule.positionId = positionId;
        rule.employeeId = employeeId;
        rule.type = type;
        rule.percentage = percentage;
        rule.fixedAmount = fixedAmount;
        rule.validPeriod = validPeriod;
        return rule;
    }

    public boolean appliesTo(LocalDate date) {
        return validPeriod.contains(date);
    }
}
