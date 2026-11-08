package com.example.backend.repository;

import com.example.backend.model.timesheet.TimeSheetEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeSheetEntryRepository extends JpaRepository<TimeSheetEntry, Long> {
    Optional<TimeSheetEntry> findByTimeSheetIdAndEmployeeId(long timeSheetId, long employeeId);

    boolean existsByTimeSheetIdAndEmployeeId(long timeSheetId, long employeeId);
}
