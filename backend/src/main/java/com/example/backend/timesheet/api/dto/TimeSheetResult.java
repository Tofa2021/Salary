package com.example.backend.timesheet.api.dto;

import com.example.backend.timesheet.domain.model.TimeSheet;

import java.time.LocalDateTime;
import java.util.List;

public record TimeSheetResult(
        Long id,
        Integer year,
        Integer month,

        Long departmentId,
        String departmentName,

        boolean approved,
        LocalDateTime approvedAt,
        String approvedByUsername,

        List<TimeSheetEntryResult> entries,

        int entryCount,
        int totalWorkedDays,
        int totalWorkedHours,
        int totalOvertimeHours,
        int totalNightHours,

        boolean canEdit,
        boolean canApprove,
        boolean canReopen

) {
    public static TimeSheetResult from(TimeSheet timeSheet, boolean canEdit, boolean canApprove, boolean canReopen) {
        List<TimeSheetEntryResult> entries = timeSheet.getEntries().stream()
                .map(TimeSheetEntryResult::from)
                .toList();

        int totalWorkedDays = entries.stream()
                .mapToInt(e -> e.workedDays() == null ? 0 : e.workedDays()).sum();
        int totalWorkedHours = entries.stream()
                .mapToInt(e -> e.workedHours() == null ? 0 : e.workedHours()).sum();
        int totalOvertimeHours = entries.stream()
                .mapToInt(e -> e.overtimeHours() == null ? 0 : e.overtimeHours()).sum();
        int totalNightHours = entries.stream()
                .mapToInt(e -> e.nightHours() == null ? 0 : e.nightHours()).sum();

        return new TimeSheetResult(
                timeSheet.getId(),
                timeSheet.getYear(),
                timeSheet.getMonth(),
                timeSheet.getDepartment() != null ? timeSheet.getDepartment().getId() : null,
                timeSheet.getDepartment() != null ? timeSheet.getDepartment().getName() : null,
                timeSheet.isApproved(),
                timeSheet.getApprovedAt(),
                timeSheet.getApprovedBy() != null ? timeSheet.getApprovedBy().getUsername() : null,
                entries,
                entries.size(),
                totalWorkedDays,
                totalWorkedHours,
                totalOvertimeHours,
                totalNightHours,
                canEdit,
                canApprove,
                canReopen
        );
    }
}
