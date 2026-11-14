package com.example.backend.payroll.application.facade;

import com.example.backend.payroll.api.BonusRuleFacade;
import com.example.backend.payroll.api.dto.bonusrule.*;
import com.example.backend.payroll.application.service.BonusRuleService;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BonusRuleFacadeImpl implements BonusRuleFacade {
    private final BonusRuleService bonusRuleService;

    @Override
    @Transactional
    public BonusRuleResult create(CreateBonusRuleCommand command) {
        return bonusRuleService.create(command);
    }

    @Override
    @Transactional(readOnly = true)
    public BonusRuleResult getById(Long id) {
        return bonusRuleService.getById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<BonusRuleResult> getAll(PageRequest page) {
        return bonusRuleService.getAll(page);
    }

    @Override
    @Transactional
    public BonusRuleResult changeBinding(Long id, ChangeBonusRuleBindingCommand command) {
        return bonusRuleService.changeBinding(id, command);
    }

    @Override
    @Transactional
    public BonusRuleResult changeValue(Long id, ChangeBonusRuleValueCommand command) {
        return bonusRuleService.changeValue(id, command);
    }

    @Override
    @Transactional
    public BonusRuleResult changePeriod(Long id, ChangeBonusRulePeriodCommand command) {
        return bonusRuleService.changePeriod(id, command);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bonusRuleService.delete(id);
    }
}
