package com.example.backend.service.timesheet;

import com.example.backend.dto.timesheet.TimeSheetResult;
import com.example.backend.exception.timesheet.TimeSheetNotFoundException;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.timesheet.TimeSheet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TimeSheetServiceGetByIdTest extends BaseTimeSheetServiceTest {
    @Test
    void getById_Exists_ReturnsResult() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        TimeSheetResult result = timeSheetService.getById(1L);

        assertTimeSheetResult(result, timeSheet);
    }

    @Test
    void getById_WithEntries_ReturnsAllEntries() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        Employee employee1 = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        Employee employee2 = employee(2L, "T-002", LocalDate.of(2026, 1, 1), null);
        timeSheet.getEntries().add(entry(10L, timeSheet, employee1, 21, 168));
        timeSheet.getEntries().add(entry(11L, timeSheet, employee2, 20, 160));
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        TimeSheetResult result = timeSheetService.getById(1L);

        assertThat(result.entries()).hasSize(2);
        assertThat(result.entryCount()).isEqualTo(2);
    }

    @Test
    void getById_EmptyTimeSheet_ReturnsEmptyEntries() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        TimeSheetResult result = timeSheetService.getById(1L);

        assertThat(result.entries()).isEmpty();
    }

    @Test
    void getById_NotFound_Throws() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.getById(99L))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }
}