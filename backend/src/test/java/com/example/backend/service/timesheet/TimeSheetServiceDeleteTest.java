package com.example.backend.service.timesheet;

import com.example.backend.timesheet.domain.exception.TimeSheetNotFoundException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimeSheetServiceDeleteTest extends BaseTimeSheetServiceTest {
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Test
    void delete_Exists_Deletes() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        timeSheetService.delete(1L);

        verify(timeSheetRepository).delete(timeSheet);
    }

    @Test
    void delete_NotFound_Throws() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.delete(99L))
                .isInstanceOf(TimeSheetNotFoundException.class);

        verify(timeSheetRepository, never()).delete(any());
    }
}
