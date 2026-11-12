package com.example.backend.service.timesheet;

import com.example.backend.timesheet.api.dto.CreateTimeSheetCommand;
import com.example.backend.timesheet.api.dto.TimeSheetEntryCommand;
import com.example.backend.timesheet.application.service.TimeSheetService;
import com.example.backend.timesheet.domain.model.*;
import com.example.backend.timesheet.domain.port.TimeSheetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
abstract class BaseTimeSheetServiceTest {
    protected final Clock clock = Clock.fixed(
            LocalDateTime.of(2026, 1, 15, 12, 0)
                    .toInstant(ZoneOffset.UTC),
            ZoneOffset.UTC);
    @Mock
    protected TimeSheetRepository timeSheetRepository;

    @InjectMocks
    protected TimeSheetService timeSheetService;

    @BeforeEach
    void setUp() {
        timeSheetService = new TimeSheetService(timeSheetRepository, clock);
    }

    protected TimeSheet timeSheet(Long id, LocalDate start, LocalDate end) {
        return TimeSheet.rehydrate(
                id, 1L, start, end,
                TimeSheetStatus.DRAFT,
                new ArrayList<>(),
                null, null);
    }

    protected TimeSheet submitted(Long id, LocalDate start, LocalDate end) {
        return TimeSheet.rehydrate(
                id, 1L,
                start,
                end,
                TimeSheetStatus.SUBMITTED,
                new ArrayList<>(),
                null,
                null
        );
    }

    protected TimeSheet approved(Long id, LocalDate start, LocalDate end, Long approvedBy) {
        return TimeSheet.rehydrate(
                id, 1L,
                start,
                end,
                TimeSheetStatus.APPROVED,
                new ArrayList<>(),
                LocalDate.now().atStartOfDay(),
                approvedBy
        );
    }

    protected TimeSheetEntry entry(Long id, LocalDate date, double hours, TimeSheetEntryType type) {
        return TimeSheetEntry.rehydrate(id, date, WorkHours.of(hours), type);
    }

    protected CreateTimeSheetCommand createRequest(LocalDate start, LocalDate end, Long employeeId) {
        return new CreateTimeSheetCommand(start, end, employeeId);
    }

    protected TimeSheetEntryCommand addEntryCommand(LocalDate date, double hours, TimeSheetEntryType type) {
        return new TimeSheetEntryCommand(date, hours, type);
    }
}
