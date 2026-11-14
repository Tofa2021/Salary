package com.example.backend.payroll.domain.accrual.port;

import com.example.backend.payroll.domain.accrual.model.BonusRule;

import java.time.LocalDate;
import java.util.Optional;

public interface BonusRuleRepository {
    Optional<BonusRule> findActiveByPositionOrEmployeeOnDate(Long positionId, Long employeeId, LocalDate date);

    BonusRule save(BonusRule rule);
}
