package com.example.backend.payroll.domain.accrual.exception;

import com.example.backend.shared.exception.NotFoundException;

public class BonusRuleNotFoundException extends NotFoundException {
    public BonusRuleNotFoundException(Long id) {
        super("BonusRule", "id", String.valueOf(id));
    }
}
