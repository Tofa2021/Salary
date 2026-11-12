package com.example.backend.timesheet.api.dto;

import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import com.example.backend.timesheet.domain.model.TimeSheetStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record TimeSheetResult(
        Long id,
        Long employeeId,
        LocalDate periodStart,
        LocalDate periodEnd,
        TimeSheetStatus status,
        List<TimeSheetEntryResult> entries,
        BigDecimal totalHours,
        Map<TimeSheetEntryType, BigDecimal> hoursByType,
        LocalDateTime approvedAt,
        Long approvedBy
) {
}
