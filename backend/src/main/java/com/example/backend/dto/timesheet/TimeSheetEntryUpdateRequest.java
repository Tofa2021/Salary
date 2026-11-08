package com.example.backend.dto.timesheet;

public record TimeSheetEntryUpdateRequest(
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
    public TimeSheetEntryUpdateRequest(Integer workedDays, Integer workedHours, Integer vacationDays, Integer sickLeaveDays, Integer businessTripDays, Integer absentDays, Integer overtimeHours, Integer nightHours) {
        this(0L, workedDays, workedHours, vacationDays, sickLeaveDays, businessTripDays, absentDays, overtimeHours, nightHours);
    }
}
