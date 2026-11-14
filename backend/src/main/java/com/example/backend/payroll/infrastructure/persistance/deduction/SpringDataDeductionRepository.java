package com.example.backend.payroll.infrastructure.persistance.deduction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SpringDataDeductionRepository extends JpaRepository<DeductionJpaEntity, Long> {
    @Query("""
            SELECT d FROM DeductionJpaEntity d
            WHERE d.employeeId = :employeeId
              AND d.validFrom <= :date
              AND d.validTo >= :date
            """)
    List<DeductionJpaEntity> findActiveByEmployeeOnDate(@Param("employeeId") Long employeeId, @Param("date") LocalDate date);
}
