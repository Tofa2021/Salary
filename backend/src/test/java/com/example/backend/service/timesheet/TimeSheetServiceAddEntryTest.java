package com.example.backend.service.timesheet;

import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.domain.exception.DayHoursExceededException;
import com.example.backend.timesheet.domain.exception.EntryOutsidePeriodException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimeSheetServiceAddEntryTest extends BaseTimeSheetServiceTest {
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Test
    void addEntry_Valid_AddsEntry() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TimeSheetResult result = timeSheetService.addEntry(1L,
                addEntryCommand(LocalDate.of(2026, 1, 15), 8, TimeSheetEntryType.REGULAR));

        assertThat(result.entries()).hasSize(1);
        assertThat(result.entries().getFirst().hours()).isEqualByComparingTo("8");
        verify(timeSheetRepository).save(any());
    }

    @Test
    void addEntry_OutsidePeriod_Throws() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.addEntry(1L,
                addEntryCommand(LocalDate.of(2026, 2, 1), 8, TimeSheetEntryType.REGULAR)))
                .isInstanceOf(EntryOutsidePeriodException.class);

        verify(timeSheetRepository, never()).save(any());
    }

    @Test
    void addEntry_DayHoursExceeded_Throws() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        timeSheetService.addEntry(1L,
                addEntryCommand(LocalDate.of(2026, 1, 15), 20, TimeSheetEntryType.REGULAR));

        assertThatThrownBy(() -> timeSheetService.addEntry(1L,
                addEntryCommand(LocalDate.of(2026, 1, 15), 8, TimeSheetEntryType.OVERTIME)))
                .isInstanceOf(DayHoursExceededException.class);
    }
}
