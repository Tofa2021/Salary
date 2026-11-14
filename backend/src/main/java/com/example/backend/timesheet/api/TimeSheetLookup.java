package com.example.backend.timesheet.api;

import com.example.backend.timesheet.api.dto.TimeSheetSummaryResult;

import java.time.LocalDate;
import java.util.Optional;

public interface TimeSheetLookup {
    Optional<TimeSheetSummaryResult> findByEmployeeAndPeriod(Long employeeId, LocalDate start, LocalDate end);
}
