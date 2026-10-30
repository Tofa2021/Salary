package com.example.backend.model.timesheet;

import com.example.backend.model.employee.Employee;
import jakarta.persistence.*;

@Entity
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
}
