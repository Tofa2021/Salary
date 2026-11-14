package com.example.backend.payroll.infrastructure.persistance.employer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SpringDataEmployerContributionRepository extends JpaRepository<EmployerContributionJpaEntity, Long> {
    @Query("""
            SELECT e FROM EmployerContributionJpaEntity e
            WHERE e.validFrom <= :date
              AND e.validTo >= :date
            """)
    List<EmployerContributionJpaEntity> findActiveOnDate(@Param("date") LocalDate date);
}
