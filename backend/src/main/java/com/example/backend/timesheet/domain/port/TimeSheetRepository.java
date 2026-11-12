package com.example.backend.timesheet.domain.port;

import com.example.backend.timesheet.domain.model.TimeSheet;

import java.util.Optional;

public interface TimeSheetRepository {
    Optional<TimeSheet> findByDepartmentAndPeriod(Long departmentId, int year, int month);
}
