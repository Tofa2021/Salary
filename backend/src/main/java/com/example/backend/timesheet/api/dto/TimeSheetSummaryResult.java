package com.example.backend.timesheet.api.dto;

import com.example.backend.timesheet.domain.model.TimeSheet;

import java.time.LocalDateTime;

public record TimeSheetSummaryResult(
        Long id,
        Integer year,
        Integer month,
        Long departmentId,
        String departmentName,
        boolean approved,
        LocalDateTime approvedAt,
        int entryCount
) {
    public static TimeSheetSummaryResult from(TimeSheet timeSheet) {
        return new TimeSheetSummaryResult(
                timeSheet.getId(),
                timeSheet.getYear(),
                timeSheet.getMonth(),
                timeSheet.getDepartment().getId(),
                timeSheet.getDepartment().getName(),
                timeSheet.isApproved(),
                timeSheet.getApprovedAt(),
                timeSheet.getEntries().size()
        );
    }
}
