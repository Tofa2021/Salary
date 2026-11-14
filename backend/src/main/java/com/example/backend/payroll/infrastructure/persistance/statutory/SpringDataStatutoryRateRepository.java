package com.example.backend.payroll.infrastructure.persistance.statutory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface SpringDataStatutoryRateRepository extends JpaRepository<StatutoryRateJpaEntity, Long> {

    @Query("""
            SELECT s FROM StatutoryRateJpaEntity s
            WHERE s.code = :code
              AND s.validFrom <= :date
              AND s.validTo >= :date
            """)
    Optional<StatutoryRateJpaEntity> findActiveByCodeOnDate(@Param("code") String code, @Param("date") LocalDate date);
}
