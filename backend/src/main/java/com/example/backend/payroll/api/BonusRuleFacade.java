package com.example.backend.payroll.api;

import com.example.backend.payroll.api.dto.bonusrule.*;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

public interface BonusRuleFacade {
    BonusRuleResult create(CreateBonusRuleCommand command);

    BonusRuleResult getById(Long id);

    PageResult<BonusRuleResult> getAll(PageRequest page);

    BonusRuleResult changeBinding(Long id, ChangeBonusRuleBindingCommand command);

    BonusRuleResult changeValue(Long id, ChangeBonusRuleValueCommand command);

    BonusRuleResult changePeriod(Long id, ChangeBonusRulePeriodCommand command);

    void delete(Long id);
}
