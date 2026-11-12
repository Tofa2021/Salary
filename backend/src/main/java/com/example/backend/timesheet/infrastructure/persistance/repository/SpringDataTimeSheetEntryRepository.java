package com.example.backend.timesheet.infrastructure.persistance.repository;

import com.example.backend.timesheet.domain.model.TimeSheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataTimeSheetEntryRepository extends JpaRepository<TimeSheetEntry, Long> {
    Optional<TimeSheetEntry> findByTimeSheetIdAndEmployeeId(long timeSheetId, long employeeId);

    boolean existsByTimeSheetIdAndEmployeeId(long timeSheetId, long employeeId);
}
