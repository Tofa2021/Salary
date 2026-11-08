package com.example.backend.dto.timesheet;

public record CreateTimeSheetRequest(
        int year,
        int month,
        Long departmentId
) {
}
