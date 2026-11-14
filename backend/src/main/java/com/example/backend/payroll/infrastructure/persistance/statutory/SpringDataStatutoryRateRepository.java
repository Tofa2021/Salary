package com.example.backend.payroll.infrastructure.persistance.statutory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<StatutoryRateJpaEntity> findByCode(String code, Pageable pageable);

    @Query("""
            SELECT COUNT(s) > 0 FROM StatutoryRateJpaEntity s
            WHERE s.code = :code
              AND s.validFrom <= :to
              AND s.validTo >= :from
              AND (:excludeId IS NULL OR s.id <> :excludeId)
            """)
    boolean existsOverlapping(
            @Param("code") String code,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("excludeId") Long excludeId
    );
}
