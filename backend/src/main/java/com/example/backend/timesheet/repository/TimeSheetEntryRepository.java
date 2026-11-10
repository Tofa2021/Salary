package com.example.backend.timesheet.repository;

import com.example.backend.timesheet.model.TimeSheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeSheetEntryRepository extends JpaRepository<TimeSheetEntry, Long> {
    Optional<TimeSheetEntry> findByTimeSheetIdAndEmployeeId(long timeSheetId, long employeeId);

    boolean existsByTimeSheetIdAndEmployeeId(long timeSheetId, long employeeId);
}
