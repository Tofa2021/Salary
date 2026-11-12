package com.example.backend.timesheet.api.dto;

import com.example.backend.timesheet.domain.model.TimeSheetEntry;

public record TimeSheetEntryResult(
        Long id,
        Long timeSheetId,
        Long employeeId,
        String employeePersonnelNumber,
        String employeeFullName,

        Integer workedDays,
        Integer workedHours,
        Integer vacationDays,
        Integer sickLeaveDays,
        Integer businessTripDays,
        Integer absentDays,
        Integer overtimeHours,
        Integer nightHours
) {
    public static TimeSheetEntryResult from(TimeSheetEntry entry) {
        return new TimeSheetEntryResult(
                entry.getId(),
                entry.getTimeSheet() != null ? entry.getTimeSheet().getId() : null,
                entry.getEmployee().getId(),
                entry.getEmployee().getPersonnelNumber(),
                entry.getEmployee().getSurname() + " " + entry.getEmployee().getName(),
                entry.getWorkedDays(),
                entry.getWorkedHours(),
                entry.getVacationDays(),
                entry.getSickLeaveDays(),
                entry.getBusinessTripDays(),
                entry.getAbsentDays(),
                entry.getOvertimeHours(),
                entry.getNightHours()
        );
    }
}
