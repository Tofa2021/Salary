package com.example.backend.timesheet.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TimeSheetEntry {
    private Long id;
    private TimeSheet timeSheet;
    private Long employeeId;

    private Integer workedDays;
    private Integer workedHours;
    private Integer vacationDays;
    private Integer sickLeaveDays;
    private Integer businessTripDays;
    private Integer absentDays;
    private Integer overtimeHours;
    private Integer nightHours;
}
