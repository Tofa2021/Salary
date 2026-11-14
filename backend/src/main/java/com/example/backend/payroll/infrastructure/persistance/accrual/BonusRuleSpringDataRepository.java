package com.example.backend.payroll.infrastructure.persistance.accrual;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BonusRuleSpringDataRepository extends JpaRepository<BonusRuleJpaEntity, Long> {
    @Query("""
            SELECT b FROM BonusRuleJpaEntity b
            WHERE (b.positionId = :positionId OR b.employeeId = :employeeId)
              AND b.validFrom <= :date
              AND b.validTo >= :date
            ORDER BY b.employeeId DESC NULLS LAST
            """)
    List<BonusRuleJpaEntity> findActiveByPositionOrEmployeeOnDate(
            @Param("positionId") Long positionId,
            @Param("employeeId") Long employeeId,
            @Param("date") LocalDate date
    );
}
