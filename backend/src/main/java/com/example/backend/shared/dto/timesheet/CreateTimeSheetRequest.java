package com.example.backend.shared.dto.timesheet;

public record CreateTimeSheetRequest(
        int year,
        int month,
        Long departmentId
) {
}
