package com.example.backend.payroll.infrastructure.persistance.base;

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
}
