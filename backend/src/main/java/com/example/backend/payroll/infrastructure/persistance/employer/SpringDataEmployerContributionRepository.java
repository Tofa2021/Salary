package com.example.backend.payroll.infrastructure.persistance.employer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataEmployerContributionRepository extends JpaRepository<EmployerContributionJpaEntity, Long> {
    @Query("""
            SELECT e FROM EmployerContributionJpaEntity e
            WHERE e.validFrom <= :date
              AND e.validTo >= :date
            """)
    List<EmployerContributionJpaEntity> findActiveOnDate(@Param("date") LocalDate date);

    @Query("""
            SELECT e FROM EmployerContributionJpaEntity e
            WHERE e.code = :code
              AND e.validFrom <= :date
              AND e.validTo >= :date
            """)
    Optional<EmployerContributionJpaEntity> findActiveByCodeOnDate(@Param("code") String code, @Param("date") LocalDate date);

    Page<EmployerContributionJpaEntity> findAllByCode(String code, Pageable pageable);

    @Query("""
            SELECT COUNT(e) > 0 FROM EmployerContributionJpaEntity e
            WHERE e.code = :code
              AND e.validFrom <= :to
              AND e.validTo >= :from
              AND (:excludeId IS NULL OR e.id <> :excludeId)
            """)
    boolean existsOverlapping(
            @Param("code") String code,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("excludeId") Long excludeId
    );
}
