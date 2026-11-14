package com.example.backend.payroll.infrastructure.persistance.base;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

interface SpringDataTariffRateRepository extends JpaRepository<TariffRateJpaEntity, Long> {

    @Query("""
            SELECT t FROM TariffRateJpaEntity t
            WHERE t.positionId = :positionId
              AND t.validFrom <= :date
              AND t.validTo >= :date
            """)
    Optional<TariffRateJpaEntity> findActiveByPositionOnDate(@Param("positionId") Long positionId, @Param("date") LocalDate date);

    Page<TariffRateJpaEntity> findByPositionId(Long positionId, Pageable pageable);

    @Query("""
            SELECT COUNT(t) > 0 FROM TariffRateJpaEntity t
            WHERE t.positionId = :positionId
              AND t.validFrom <= :to
              AND t.validTo >= :from
              AND (:excludeId IS NULL OR t.id <> :excludeId)
            """)
    boolean existsOverlapping(
            @Param("positionId") Long positionId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("excludeId") Long excludeId
    );
}
