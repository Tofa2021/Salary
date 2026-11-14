package com.example.backend.payroll.domain.accrual.exception;

import com.example.backend.shared.exception.InvalidStateException;

public class InvalidBonusRuleException extends InvalidStateException {
    public InvalidBonusRuleException(String message) {
        super(message);
    }
}
