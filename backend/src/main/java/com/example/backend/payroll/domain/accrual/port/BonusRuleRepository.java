package com.example.backend.payroll.domain.accrual.port;

import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;

import java.time.LocalDate;
import java.util.Optional;

public interface BonusRuleRepository {
    Optional<BonusRule> findActiveByPositionOrEmployeeOnDate(Long positionId, Long employeeId, LocalDate date);

    BonusRule save(BonusRule rule);

    Optional<BonusRule> findById(Long id);

    PageResult<BonusRule> findAll(PageRequest pageRequest);

    void delete(BonusRule rule);
}
