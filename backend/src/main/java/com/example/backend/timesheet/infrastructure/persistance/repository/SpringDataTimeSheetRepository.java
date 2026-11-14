package com.example.backend.timesheet.infrastructure.persistance.repository;

import com.example.backend.timesheet.infrastructure.persistance.entity.TimeSheetJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataTimeSheetRepository extends JpaRepository<TimeSheetJpaEntity, Long> {
    Page<TimeSheetJpaEntity> findByEmployeeId(Long employeeId, Pageable pageable);

    Page<TimeSheetJpaEntity> findByPeriodStartGreaterThanEqualAndPeriodEndLessThanEqual(
            LocalDate periodStart,
            LocalDate periodEnd,
            Pageable pageable
    );

    @Query("""
            SELECT t FROM TimeSheetJpaEntity t
            WHERE t.employeeId = :employeeId
              AND t.periodStart <= :end
              AND t.periodEnd >= :start
            """)
    Optional<TimeSheetJpaEntity> findByEmployeeIdAndPeriod(
            @Param("employeeId") Long employeeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
            SELECT t FROM TimeSheetJpaEntity t
            WHERE t.periodStart <= :end
              AND t.periodEnd >= :start
            """)
    List<TimeSheetJpaEntity> findAllByPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
