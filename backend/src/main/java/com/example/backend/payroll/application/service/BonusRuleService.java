package com.example.backend.payroll.application.service;

import com.example.backend.payroll.api.dto.bonusrule.*;
import com.example.backend.payroll.domain.accrual.exception.BonusRuleNotFoundException;
import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.port.BonusRuleRepository;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BonusRuleService {
    private final BonusRuleRepository bonusRuleRepository;

    public BonusRuleResult create(CreateBonusRuleCommand command) {
        BonusRule rule = BonusRule.create(
                command.positionId(),
                command.employeeId(),
                command.type(),
                command.percentage(),
                command.fixedAmount(),
                command.validPeriod()
        );
        return toResult(bonusRuleRepository.save(rule));
    }

    public BonusRuleResult getById(Long id) {
        return toResult(findById(id));
    }

    public PageResult<BonusRuleResult> getAll(PageRequest pageRequest) {
        return bonusRuleRepository.findAll(pageRequest).map(this::toResult);
    }

    public BonusRuleResult changeBinding(Long id, ChangeBonusRuleBindingCommand command) {
        BonusRule rule = findById(id);
        if (command.positionId() != null) {
            rule.bindToPosition(command.positionId());
        } else {
            rule.bindToEmployee(command.employeeId());
        }
        return toResult(bonusRuleRepository.save(rule));
    }

    public BonusRuleResult changeValue(Long id, ChangeBonusRuleValueCommand command) {
        BonusRule rule = findById(id);
        rule.changeType(command.type(), command.percentage(), command.fixedAmount());
        return toResult(bonusRuleRepository.save(rule));
    }

    public BonusRuleResult changePeriod(Long id, ChangeBonusRulePeriodCommand command) {
        BonusRule rule = findById(id);
        rule.changeValidPeriod(command.validPeriod());
        return toResult(bonusRuleRepository.save(rule));
    }

    public void delete(Long id) {
        bonusRuleRepository.delete(findById(id));
    }

    private BonusRule findById(Long id) {
        return bonusRuleRepository.findById(id)
                .orElseThrow(() -> new BonusRuleNotFoundException(id));
    }

    private BonusRuleResult toResult(BonusRule rule) {
        return new BonusRuleResult(
                rule.getId(),
                rule.getPositionId(),
                rule.getEmployeeId(),
                rule.getType(),
                rule.getPercentage(),
                rule.getFixedAmount(),
                rule.getValidPeriod()
        );
    }
}
