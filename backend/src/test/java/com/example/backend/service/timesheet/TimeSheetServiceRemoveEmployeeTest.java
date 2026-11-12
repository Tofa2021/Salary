package com.example.backend.service.timesheet;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.timesheet.domain.exception.TimeSheetAlreadyApprovedException;
import com.example.backend.timesheet.domain.exception.TimeSheetEntryNotFoundException;
import com.example.backend.timesheet.domain.exception.TimeSheetNotFoundException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntry;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TimeSheetServiceRemoveEmployeeTest extends BaseTimeSheetServiceTest {
    @Test
    void removeEmployee_Valid_DeletesEntry() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        Employee employee = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        TimeSheetEntry entry = entry(10L, timeSheet, employee, 21, 168);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetEntryRepository.findByTimeSheetIdAndEmployeeId(1L, 1L))
                .thenReturn(Optional.of(entry));

        timeSheetService.removeEmployeeFromTimeSheet(1L, 1L);

        verify(timeSheetEntryRepository).delete(entry);
    }

    @Test
    void removeEmployee_TimeSheetNotFound_Throws() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.removeEmployeeFromTimeSheet(99L, 1L))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }

    @Test
    void removeEmployee_EntryNotFound_Throws() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetEntryRepository.findByTimeSheetIdAndEmployeeId(1L, 99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.removeEmployeeFromTimeSheet(1L, 99L))
                .isInstanceOf(TimeSheetEntryNotFoundException.class);
    }

    @Test
    void removeEmployee_TimeSheetApproved_Throws() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, true);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.removeEmployeeFromTimeSheet(1L, 1L))
                .isInstanceOf(TimeSheetAlreadyApprovedException.class);
    }
}
