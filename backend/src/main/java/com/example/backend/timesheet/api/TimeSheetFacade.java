package com.example.backend.timesheet.api;

import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import com.example.backend.timesheet.api.dto.CreateTimeSheetCommand;
import com.example.backend.timesheet.api.dto.TimeSheetEntryCommand;
import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.api.dto.TimeSheetSummaryResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeSheetFacade {
    List<TimeSheetSummaryResult> findHoursSummaries(LocalDate start, LocalDate end);

    Optional<TimeSheetSummaryResult> findHoursSummaryOptional(Long employeeId, LocalDate start, LocalDate end);

    PageResult<TimeSheetResult> getByPeriod(LocalDate start, LocalDate end, PageRequest pageRequest);

    PageResult<TimeSheetResult> getByEmployee(Long employeeId, PageRequest pageRequest);

    Optional<TimeSheetResult> findByIdOptional(Long id);

    TimeSheetResult getById(Long id);

    void delete(Long timeSheetId);

    TimeSheetResult reject(Long timeSheetId);

    TimeSheetResult approve(Long timeSheetId, Long approvedBy);

    TimeSheetResult submit(Long timeSheetId);

    TimeSheetResult removeEntry(Long timeSheetId, Long entryId);

    TimeSheetResult addEntry(Long timesheetId, TimeSheetEntryCommand command);

    TimeSheetResult create(CreateTimeSheetCommand command);
}
