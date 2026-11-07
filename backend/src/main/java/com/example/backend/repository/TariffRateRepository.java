package com.example.backend.repository;

import com.example.backend.model.employee.TariffRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TariffRateRepository extends JpaRepository<TariffRate, Long> {
    boolean existsByEffectiveFrom(LocalDate effectiveFrom);

    @Query("""
            SELECT t FROM TariffRate t
            WHERE t.effectiveFrom <= :date
              AND (t.effectiveTo IS NULL OR t.effectiveTo >= :date)
            """)
    Optional<TariffRate> findEffectiveOn(@Param("date") LocalDate date);

    Optional<TariffRate> findFirstByEffectiveToIsNullOrderByEffectiveFromDesc();
}
