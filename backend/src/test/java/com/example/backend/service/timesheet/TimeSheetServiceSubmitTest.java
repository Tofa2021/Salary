package com.example.backend.service.timesheet;

import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.domain.exception.EmptyTimeSheetException;
import com.example.backend.timesheet.domain.exception.InvalidTimeSheetStatusException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import com.example.backend.timesheet.domain.model.TimeSheetStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimeSheetServiceSubmitTest extends BaseTimeSheetServiceTest {
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Test
    void submit_WithEntries_SetsSubmitted() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        timeSheet.getEntries().add(entry(10L, LocalDate.of(2026, 1, 15), 8, TimeSheetEntryType.REGULAR));
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TimeSheetResult result = timeSheetService.submit(1L);

        assertThat(result.status()).isEqualTo(TimeSheetStatus.SUBMITTED);
        verify(timeSheetRepository).save(any());
    }

    @Test
    void submit_Empty_Throws() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.submit(1L))
                .isInstanceOf(EmptyTimeSheetException.class);

        verify(timeSheetRepository, never()).save(any());
    }

    @Test
    void submit_Approved_Throws() {
        TimeSheet timeSheet = approved(1L, START, END, 99L);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.submit(1L))
                .isInstanceOf(InvalidTimeSheetStatusException.class);
    }
}
