package com.example.backend.timesheet.application.service;

import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import com.example.backend.timesheet.api.dto.*;
import com.example.backend.timesheet.domain.exception.TimeSheetNotFoundException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntry;
import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import com.example.backend.timesheet.domain.model.WorkHours;
import com.example.backend.timesheet.domain.port.TimeSheetRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TimeSheetService {
    private final TimeSheetRepository timeSheetRepository;
    private final Clock clock;

    public TimeSheetResult create(CreateTimeSheetCommand command) {
        TimeSheet timeSheet = TimeSheet.create(command.employeeId(), command.periodStart(), command.periodEnd());
        return toResult(timeSheetRepository.save(timeSheet));
    }

    public TimeSheetResult addEntry(Long timesheetId, TimeSheetEntryCommand command) {
        TimeSheet timeSheet = findById(timesheetId);
        timeSheet.addEntry(TimeSheetEntry.create(command.date(), WorkHours.of(command.hours()), command.type()));
        return toResult(timeSheetRepository.save(timeSheet));
    }

    public TimeSheetResult removeEntry(Long timeSheetId, Long entryId) {
        TimeSheet timeSheet = findById(timeSheetId);
        timeSheet.removeEntry(entryId);
        return toResult(timeSheetRepository.save(timeSheet));
    }

    public TimeSheetResult submit(Long timeSheetId) {
        TimeSheet timeSheet = findById(timeSheetId);
        timeSheet.submit();
        return toResult(timeSheetRepository.save(timeSheet));
    }

    public TimeSheetResult approve(Long timeSheetId, Long approvedBy) {
        TimeSheet timeSheet = findById(timeSheetId);
        timeSheet.approve(approvedBy, LocalDateTime.now(clock));
        return toResult(timeSheetRepository.save(timeSheet));
    }

    public TimeSheetResult reject(Long timeSheetId) {
        TimeSheet timeSheet = findById(timeSheetId);
        timeSheet.reject();
        return toResult(timeSheetRepository.save(timeSheet));
    }

    public void delete(Long timeSheetId) {
        TimeSheet timeSheet = findById(timeSheetId);
        timeSheetRepository.delete(timeSheet);
    }

    public TimeSheetResult getById(Long id) {
        return toResult(findById(id));
    }

    public Optional<TimeSheetResult> findByIdOptional(Long id) {
        return timeSheetRepository.findById(id).map(this::toResult);
    }

    public PageResult<TimeSheetResult> getByEmployee(Long employeeId, PageRequest pageRequest) {
        return timeSheetRepository.findByEmployeeId(employeeId, pageRequest).map(this::toResult);
    }

    public PageResult<TimeSheetResult> getByPeriod(LocalDate start, LocalDate end, PageRequest pageRequest) {
        return timeSheetRepository.findByPeriod(start, end, pageRequest).map(this::toResult);
    }

    public Optional<TimeSheetSummaryResult> findHoursSummaryOptional(Long employeeId, LocalDate start, LocalDate end) {
        return timeSheetRepository.findByEmployeeIdAndPeriod(employeeId, start, end)
                .map(this::toHoursSummary);
    }

    public List<TimeSheetSummaryResult> findHoursSummaries(LocalDate start, LocalDate end) {
        return timeSheetRepository.findAllByPeriod(start, end).stream()
                .map(this::toHoursSummary)
                .toList();
    }

    private TimeSheet findById(Long id) {
        return timeSheetRepository.findById(id).orElseThrow(() -> new TimeSheetNotFoundException(id));
    }

    private TimeSheetResult toResult(TimeSheet timeSheet) {
        Map<TimeSheetEntryType, BigDecimal> byType = timeSheet.getEntries().stream()
                .collect(Collectors.groupingBy(
                        TimeSheetEntry::getType,
                        Collectors.reducing(BigDecimal.ZERO, e -> e.getHours().value(), BigDecimal::add)
                ));
        return new TimeSheetResult(
                timeSheet.getId(),
                timeSheet.getEmployeeId(),
                timeSheet.getPeriodStart(),
                timeSheet.getPeriodEnd(),
                timeSheet.getStatus(),
                timeSheet.getEntries().stream().map(this::toResult).toList(),
                timeSheet.totalHours().value(),
                byType,
                timeSheet.getApprovedAt(),
                timeSheet.getApprovedBy()
        );
    }

    private TimeSheetEntryResult toResult(TimeSheetEntry entry) {
        return new TimeSheetEntryResult(
                entry.getId(),
                entry.getDate(),
                entry.getHours().value(),
                entry.getType()
        );
    }

    private TimeSheetSummaryResult toHoursSummary(TimeSheet timeSheet) {
        Map<TimeSheetEntryType, BigDecimal> byType = timeSheet.getEntries().stream()
                .collect(Collectors.groupingBy(
                        TimeSheetEntry::getType,
                        Collectors.reducing(BigDecimal.ZERO, e -> e.getHours().value(), BigDecimal::add)
                ));
        return new TimeSheetSummaryResult(
                timeSheet.getEmployeeId(),
                timeSheet.getPeriodStart(),
                timeSheet.getPeriodEnd(),
                timeSheet.totalHours().value(),
                byType
        );
    }
}
