package com.example.backend.service.timesheet;

import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.domain.exception.InvalidTimeSheetStatusException;
import com.example.backend.timesheet.domain.exception.TimeSheetNotFoundException;
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

class TimeSheetServiceApproveTest extends BaseTimeSheetServiceTest {
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Test
    void approve_Submitted_SetsApproved() {
        TimeSheet timeSheet = submitted(1L, START, END);
        timeSheet.getEntries().add(entry(10L, LocalDate.of(2026, 1, 15), 8, TimeSheetEntryType.REGULAR));
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TimeSheetResult result = timeSheetService.approve(1L, 99L);

        assertThat(result.status()).isEqualTo(TimeSheetStatus.APPROVED);
        assertThat(result.approvedAt()).isNotNull();
        assertThat(result.approvedBy()).isEqualTo(99L);
        verify(timeSheetRepository).save(any());
    }

    @Test
    void approve_NotFound_Throws() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.approve(99L, 1L))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }

    @Test
    void approve_Draft_Throws() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.approve(1L, 99L))
                .isInstanceOf(InvalidTimeSheetStatusException.class);

        verify(timeSheetRepository, never()).save(any());
    }

    @Test
    void approve_AlreadyApproved_Throws() {
        TimeSheet timeSheet = approved(1L, START, END, 99L);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.approve(1L, 99L))
                .isInstanceOf(InvalidTimeSheetStatusException.class);
    }
}
