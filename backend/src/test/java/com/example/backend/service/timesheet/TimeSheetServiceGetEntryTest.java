package com.example.backend.service.timesheet;

import com.example.backend.dto.timesheet.TimeSheetEntryResult;
import com.example.backend.exception.timesheet.TimeSheetEntryNotFoundException;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.timesheet.TimeSheet;
import com.example.backend.model.timesheet.TimeSheetEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TimeSheetServiceGetEntryTest extends BaseTimeSheetServiceTest {
    @Test
    void getEntry_Exists_ReturnsResult() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        Employee employee = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        TimeSheetEntry entry = entry(10L, timeSheet, employee, 21, 168);
        when(timeSheetEntryRepository.findByTimeSheetIdAndEmployeeId(1L, 1L))
                .thenReturn(Optional.of(entry));

        TimeSheetEntryResult result = timeSheetService.getEntry(1L, 1L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.workedDays()).isEqualTo(21);
        assertThat(result.workedHours()).isEqualTo(168);
    }

    @Test
    void getEntry_NotFound_Throws() {
        when(timeSheetEntryRepository.findByTimeSheetIdAndEmployeeId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.getEntry(1L, 99L))
                .isInstanceOf(TimeSheetEntryNotFoundException.class);
    }
}