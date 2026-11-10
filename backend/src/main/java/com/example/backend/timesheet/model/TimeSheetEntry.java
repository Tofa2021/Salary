package com.example.backend.timesheet.model;

import com.example.backend.employee.domain.model.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "time_sheet_entries")
public class TimeSheetEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TimeSheet timeSheet;

    @ManyToOne
    private Employee employee;

    private Integer workedDays;
    private Integer workedHours;
    private Integer vacationDays;
    private Integer sickLeaveDays;
    private Integer businessTripDays;
    private Integer absentDays;
    private Integer overtimeHours;
    private Integer nightHours;

    public TimeSheetEntry(
            TimeSheet timeSheet,
            Employee employee,
            Integer workedDays,
            Integer workedHours,
            Integer vacationDays,
            Integer sickLeaveDays,
            Integer businessTripDays,
            Integer absentDays,
            Integer overtimeHours,
            Integer nightHours
    ) {
        this.timeSheet = timeSheet;
        this.employee = employee;
        this.workedDays = workedDays;
        this.workedHours = workedHours;
        this.vacationDays = vacationDays;
        this.sickLeaveDays = sickLeaveDays;
        this.businessTripDays = businessTripDays;
        this.absentDays = absentDays;
        this.overtimeHours = overtimeHours;
        this.nightHours = nightHours;
    }
}
