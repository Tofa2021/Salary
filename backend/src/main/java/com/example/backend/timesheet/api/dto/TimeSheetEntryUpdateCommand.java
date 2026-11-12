package com.example.backend.timesheet.api.dto;

public record TimeSheetEntryUpdateCommand(
        Long id,
        Integer workedDays,
        Integer workedHours,
        Integer vacationDays,
        Integer sickLeaveDays,
        Integer businessTripDays,
        Integer absentDays,
        Integer overtimeHours,
        Integer nightHours
) {
    public TimeSheetEntryUpdateCommand(Integer workedDays, Integer workedHours, Integer vacationDays, Integer sickLeaveDays, Integer businessTripDays, Integer absentDays, Integer overtimeHours, Integer nightHours) {
        this(0L, workedDays, workedHours, vacationDays, sickLeaveDays, businessTripDays, absentDays, overtimeHours, nightHours);
    }
}
