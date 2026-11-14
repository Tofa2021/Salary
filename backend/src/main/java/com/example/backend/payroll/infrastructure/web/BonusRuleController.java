package com.example.backend.payroll.infrastructure.web;

import com.example.backend.payroll.api.BonusRuleFacade;
import com.example.backend.payroll.api.dto.bonusrule.*;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bonus-rules")
@RequiredArgsConstructor
public class BonusRuleController {
    private final BonusRuleFacade bonusRuleFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BonusRuleResult create(@RequestBody CreateBonusRuleCommand command) {
        return bonusRuleFacade.create(command);
    }

    @GetMapping("/{id}")
    public BonusRuleResult get(@PathVariable Long id) {
        return bonusRuleFacade.getById(id);
    }

    @GetMapping
    public PageResult<BonusRuleResult> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return bonusRuleFacade.getAll(new PageRequest(page, size));
    }

    @PatchMapping("/{id}/binding")
    public BonusRuleResult changeBinding(
            @PathVariable Long id,
            @RequestBody ChangeBonusRuleBindingCommand command
    ) {
        return bonusRuleFacade.changeBinding(id, command);
    }

    @PatchMapping("/{id}/value")
    public BonusRuleResult changeValue(
            @PathVariable Long id,
            @RequestBody ChangeBonusRuleValueCommand command
    ) {
        return bonusRuleFacade.changeValue(id, command);
    }

    @PatchMapping("/{id}/period")
    public BonusRuleResult changePeriod(
            @PathVariable Long id,
            @RequestBody ChangeBonusRulePeriodCommand command
    ) {
        return bonusRuleFacade.changePeriod(id, command);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        bonusRuleFacade.delete(id);
    }
}
