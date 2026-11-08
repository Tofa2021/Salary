package com.example.backend.service.timesheet;

import com.example.backend.dto.timesheet.TimeSheetResult;
import com.example.backend.exception.timesheet.EmptyTimeSheetException;
import com.example.backend.exception.timesheet.TimeSheetAlreadyApprovedException;
import com.example.backend.exception.timesheet.TimeSheetNotFoundException;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.timesheet.TimeSheet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class TimeSheetServiceApproveTest extends BaseTimeSheetServiceTest {
    @Test
    void approve_Valid_SetsApproved() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        Employee employee = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        timeSheet.getEntries().add(entry(10L, timeSheet, employee, 21, 168));
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TimeSheetResult result = timeSheetService.approve(1L);

        assertThat(result.approved()).isTrue();
        assertThat(result.approvedAt()).isNotNull();
        verify(timeSheetRepository).save(any());
    }

    @Test
    void approve_NotFound_Throws() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.approve(99L))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }

    @Test
    void approve_AlreadyApproved_Throws() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, true);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.approve(1L))
                .isInstanceOf(TimeSheetAlreadyApprovedException.class);
    }

    @Test
    void approve_EmptyEntries_Throws() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.approve(1L))
                .isInstanceOf(EmptyTimeSheetException.class);
    }
}
