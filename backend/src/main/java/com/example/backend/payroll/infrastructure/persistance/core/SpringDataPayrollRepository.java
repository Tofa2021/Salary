package com.example.backend.payroll.infrastructure.persistance.core;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataPayrollRepository extends JpaRepository<PayrollJpaEntity, Long> {
    Optional<PayrollJpaEntity> findByEmployeeIdAndPeriodYearAndPeriodMonth(Long employeeId, int periodYear, int periodMonth);

    List<PayrollJpaEntity> findByPeriodYearAndPeriodMonth(int periodYear, int periodMonth);
}
