package com.example.backend.exception.timesheet;

import com.example.backend.exception.NotFoundException;

import java.util.Map;

public class TimeSheetNotFoundException extends NotFoundException {
    public TimeSheetNotFoundException(Long id) {
        super("TimeSheet", "id", String.valueOf(id));
    }

    public TimeSheetNotFoundException(Long departmentId, int year, int month) {
        super("TimeSheet", Map.of(
                "departmentId", String.valueOf(departmentId),
                "year", String.valueOf(year),
                "month", String.valueOf(month)
        ));
    }
}
