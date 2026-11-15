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

    public void update(
            Long positionId,
            Long employeeId,
            BonusType type,
            Percentage percentage,
            Money fixedAmount,
            DateRange validPeriod
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

        this.positionId = positionId;
        this.employeeId = employeeId;
        this.type = type;
        this.percentage = percentage;
        this.fixedAmount = fixedAmount;
        this.validPeriod = validPeriod;
    }

    public void changeType(BonusType type, Percentage percentage, Money fixedAmount) {
        if (type == null) {
            throw new InvalidBonusRuleException("type is required");
        }
        if (type == BonusType.FIXED && fixedAmount == null) {
            throw new InvalidBonusRuleException("fixedAmount required for FIXED");
        }
        if (type != BonusType.FIXED && percentage == null) {
            throw new InvalidBonusRuleException("percentage required for PERCENT_*");
        }
        this.type = type;
        this.percentage = percentage;
        this.fixedAmount = fixedAmount;
    }

    public void changeValidPeriod(DateRange validPeriod) {
        if (validPeriod == null) {
            throw new InvalidBonusRuleException("validPeriod is required");
        }
        this.validPeriod = validPeriod;
    }

    public void bindToPosition(Long positionId) {
        if (positionId == null) {
            throw new InvalidBonusRuleException("positionId is required");
        }
        this.positionId = positionId;
        this.employeeId = null;
    }

    public void bindToEmployee(Long employeeId) {
        if (employeeId == null) {
            throw new InvalidBonusRuleException("employeeId is required");
        }
        this.employeeId = employeeId;
        this.positionId = null;
    }
}
