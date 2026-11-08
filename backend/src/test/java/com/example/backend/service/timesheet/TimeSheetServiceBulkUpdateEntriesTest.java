package com.example.backend.service.timesheet;

import com.example.backend.dto.timesheet.TimeSheetEntryResult;
import com.example.backend.dto.timesheet.TimeSheetEntryUpdateRequest;
import com.example.backend.exception.timesheet.TimeSheetAlreadyApprovedException;
import com.example.backend.exception.timesheet.TimeSheetNotFoundException;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.timesheet.TimeSheet;
import com.example.backend.model.timesheet.TimeSheetEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TimeSheetServiceBulkUpdateEntriesTest extends BaseTimeSheetServiceTest {
    @Test
    void bulkUpdateEntries_Valid_UpdatesAll() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        Employee employee1 = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        Employee employee2 = employee(2L, "T-002", LocalDate.of(2026, 1, 1), null);
        TimeSheetEntry entry1 = entry(10L, timeSheet, employee1, 0, 0);
        TimeSheetEntry entry2 = entry(11L, timeSheet, employee2, 0, 0);
        timeSheet.getEntries().addAll(List.of(entry1, entry2));

        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetEntryRepository.findAllById(List.of(10L, 11L)))
                .thenReturn(List.of(entry1, entry2));

        List<TimeSheetEntryResult> result = timeSheetService.bulkUpdateEntries(1L, List.of(
                new TimeSheetEntryUpdateRequest(10L, 21, 168, 0, 0, 0, 0, 0, 0),
                new TimeSheetEntryUpdateRequest(11L, 20, 160, 0, 0, 0, 0, 0, 0)
        ));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).workedDays()).isEqualTo(21);
        assertThat(result.get(1).workedDays()).isEqualTo(20);
    }

    @Test
    void bulkUpdateEntries_EmptyList_ReturnsEmpty() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        List<TimeSheetEntryResult> result = timeSheetService.bulkUpdateEntries(1L, List.of());

        assertThat(result).isEmpty();
    }

    @Test
    void bulkUpdateEntries_TimeSheetNotFound_Throws() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.bulkUpdateEntries(99L, List.of()))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }

    @Test
    void bulkUpdateEntries_TimeSheetApproved_Throws() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, true);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.bulkUpdateEntries(1L, List.of()))
                .isInstanceOf(TimeSheetAlreadyApprovedException.class);
    }
}
