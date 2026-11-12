package com.example.backend.service.timesheet;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.timesheet.api.dto.TimeSheetEntryResult;
import com.example.backend.timesheet.api.dto.TimeSheetEntryUpdateCommand;
import com.example.backend.timesheet.domain.exception.InvalidTimeSheetEntryException;
import com.example.backend.timesheet.domain.exception.TimeSheetAlreadyApprovedException;
import com.example.backend.timesheet.domain.exception.TimeSheetEntryNotFoundException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimeSheetServiceUpdateEntryTest extends BaseTimeSheetServiceTest {
    private TimeSheetEntry prepare(boolean approved) {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, approved);
        Employee employee = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        TimeSheetEntry entry = entry(10L, timeSheet, employee, 0, 0);
        when(timeSheetEntryRepository.findById(10L)).thenReturn(Optional.of(entry));
        return entry;
    }

    @Test
    void updateEntry_Valid_UpdatesFields() {
        prepare(false);
        when(timeSheetEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TimeSheetEntryResult result = timeSheetService.updateEntry(10L, updateRequestWithoutId(21, 168));

        assertThat(result.workedDays()).isEqualTo(21);
        assertThat(result.workedHours()).isEqualTo(168);
        verify(timeSheetEntryRepository).save(any());
    }

    @Test
    void updateEntry_PartialNulls_OnlyFilledChanged() {
        prepare(false);
        when(timeSheetEntryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TimeSheetEntryResult result = timeSheetService.updateEntry(10L, updateRequestWithoutId(15, null));

        assertThat(result.workedDays()).isEqualTo(15);
    }

    @Test
    void updateEntry_NotFound_Throws() {
        when(timeSheetEntryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.updateEntry(99L, updateRequestWithoutId(21, 168)))
                .isInstanceOf(TimeSheetEntryNotFoundException.class);

        verify(timeSheetEntryRepository, never()).save(any());
    }

    @Test
    void updateEntry_TimeSheetApproved_Throws() {
        prepare(true);

        assertThatThrownBy(() -> timeSheetService.updateEntry(10L, updateRequestWithoutId(21, 168)))
                .isInstanceOf(TimeSheetAlreadyApprovedException.class);

        verify(timeSheetEntryRepository, never()).save(any());
    }

    @Test
    void updateEntry_OvertimeGreaterThanWorked_Throws() {
        prepare(false);

        assertThatThrownBy(() -> timeSheetService.updateEntry(10L,
                new TimeSheetEntryUpdateCommand(21, 168, 0, 0, 0, 0, 200, 0)))
                .isInstanceOf(InvalidTimeSheetEntryException.class);
    }

    @Test
    void updateEntry_NightGreaterThanWorked_Throws() {
        prepare(false);

        assertThatThrownBy(() -> timeSheetService.updateEntry(10L,
                new TimeSheetEntryUpdateCommand(21, 168, 0, 0, 0, 0, 0, 200)))
                .isInstanceOf(InvalidTimeSheetEntryException.class);
    }

    @Test
    void updateEntry_NegativeValues_Throws() {
        prepare(false);

        assertThatThrownBy(() -> timeSheetService.updateEntry(10L,
                new TimeSheetEntryUpdateCommand(-1, 0, 0, 0, 0, 0, 0, 0)))
                .isInstanceOf(InvalidTimeSheetEntryException.class);
    }
}
