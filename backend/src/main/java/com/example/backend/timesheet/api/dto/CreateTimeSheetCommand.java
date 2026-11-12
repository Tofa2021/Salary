package com.example.backend.timesheet.api.dto;

import java.time.LocalDate;

public record CreateTimeSheetCommand(
        LocalDate periodStart,
        LocalDate periodEnd,
        Long employeeId
) {
}
