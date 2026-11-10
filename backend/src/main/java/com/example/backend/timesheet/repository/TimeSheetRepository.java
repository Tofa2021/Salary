package com.example.backend.timesheet.repository;

import com.example.backend.timesheet.model.TimeSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TimeSheetRepository extends JpaRepository<TimeSheet, Long> {
    @Query("""
            SELECT t FROM TimeSheet t
            WHERE t.department.id = :departmentId
              AND t.year = :year
              AND t.month = :month
            """)
    Optional<TimeSheet> findByDepartmentAndPeriod(
            @Param("departmentId") Long departmentId,
            @Param("year") int year,
            @Param("month") int month
    );

    List<TimeSheet> findAllByYearAndMonth(int year, int month);
}
