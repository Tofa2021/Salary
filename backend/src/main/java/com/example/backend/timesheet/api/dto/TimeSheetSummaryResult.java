package com.example.backend.timesheet.api.dto;

import com.example.backend.timesheet.domain.model.TimeSheetEntryType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public record TimeSheetSummaryResult(
        Long employeeId,
        LocalDate periodStart,
        LocalDate periodEnd,
        BigDecimal totalHours,
        Map<TimeSheetEntryType, BigDecimal> hoursByType
) {
}
