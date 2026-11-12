package com.example.backend.service.timesheet;

import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.domain.exception.InvalidTimeSheetStatusException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TimeSheetServiceRejectTest extends BaseTimeSheetServiceTest {
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Test
    void reject_Submitted_SetsRejected() {
        TimeSheet timeSheet = submitted(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));
        when(timeSheetRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TimeSheetResult result = timeSheetService.reject(1L);

        assertThat(result.status()).isEqualTo(TimeSheetStatus.REJECTED);
        verify(timeSheetRepository).save(any());
    }

    @Test
    void reject_Draft_Throws() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThatThrownBy(() -> timeSheetService.reject(1L))
                .isInstanceOf(InvalidTimeSheetStatusException.class);
    }
}
