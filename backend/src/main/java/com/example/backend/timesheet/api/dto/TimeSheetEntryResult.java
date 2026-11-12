package com.example.backend.timesheet.api.dto;

import com.example.backend.timesheet.domain.model.TimeSheetEntryType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimeSheetEntryResult(
        Long id,
        LocalDate date,
        BigDecimal hours,
        TimeSheetEntryType type
) {
}
