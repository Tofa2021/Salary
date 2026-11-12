package com.example.backend.timesheet.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TimeSheet {
    private Long id;

    private int year;
    private int month;

    private Long departmentId;

    private List<TimeSheetEntry> entries;

    private boolean approved;
    private LocalDateTime approvedAt;

    private Long approvedByUserId;
}
