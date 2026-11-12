package com.example.backend.timesheet.api.dto;

public record CreateTimeSheetCommand(
        int year,
        int month,
        Long departmentId
) {
}
