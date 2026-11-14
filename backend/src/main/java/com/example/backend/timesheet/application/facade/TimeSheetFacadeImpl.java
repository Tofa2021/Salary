package com.example.backend.timesheet.application.facade;

import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import com.example.backend.timesheet.api.TimeSheetFacade;
import com.example.backend.timesheet.api.dto.CreateTimeSheetCommand;
import com.example.backend.timesheet.api.dto.TimeSheetEntryCommand;
import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.api.dto.TimeSheetSummaryResult;
import com.example.backend.timesheet.application.service.TimeSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TimeSheetFacadeImpl implements TimeSheetFacade {
    private final TimeSheetService timeSheetService;

    @Override
    @Transactional(readOnly = true)
    public List<TimeSheetSummaryResult> findHoursSummaries(LocalDate start, LocalDate end) {
        return timeSheetService.findHoursSummaries(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSheetSummaryResult> findHoursSummaryOptional(Long employeeId, LocalDate start, LocalDate end) {
        return timeSheetService.findHoursSummaryOptional(employeeId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<TimeSheetResult> getByPeriod(LocalDate start, LocalDate end, PageRequest pageRequest) {
        return timeSheetService.getByPeriod(start, end, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<TimeSheetResult> getByEmployee(Long employeeId, PageRequest pageRequest) {
        return timeSheetService.getByEmployee(employeeId, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TimeSheetResult> findByIdOptional(Long id) {
        return timeSheetService.findByIdOptional(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSheetResult getById(Long id) {
        return timeSheetService.getById(id);
    }

    @Override
    @Transactional
    public void delete(Long timeSheetId) {
        timeSheetService.delete(timeSheetId);
    }

    @Override
    @Transactional
    public TimeSheetResult reject(Long timeSheetId) {
        return timeSheetService.reject(timeSheetId);
    }

    @Override
    @Transactional
    public TimeSheetResult approve(Long timeSheetId, Long approvedBy) {
        return timeSheetService.approve(timeSheetId, approvedBy);
    }

    @Override
    @Transactional
    public TimeSheetResult submit(Long timeSheetId) {
        return timeSheetService.submit(timeSheetId);
    }

    @Override
    @Transactional
    public TimeSheetResult removeEntry(Long timeSheetId, Long entryId) {
        return timeSheetService.removeEntry(timeSheetId, entryId);
    }

    @Override
    @Transactional
    public TimeSheetResult addEntry(Long timesheetId, TimeSheetEntryCommand command) {
        return timeSheetService.addEntry(timesheetId, command);
    }

    @Override
    @Transactional
    public TimeSheetResult create(CreateTimeSheetCommand command) {
        return timeSheetService.create(command);
    }
}
