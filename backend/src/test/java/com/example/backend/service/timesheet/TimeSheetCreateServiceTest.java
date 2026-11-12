package com.example.backend.service.timesheet;

import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TimeSheetCreateServiceTest extends BaseTimeSheetServiceTest {

    @Test
    void create_Valid_ReturnsDraft() {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        when(timeSheetRepository.save(any(TimeSheet.class))).thenAnswer(inv -> {
            TimeSheet timeSheet = inv.getArgument(0);
            return TimeSheet.rehydrate(1L, timeSheet.getEmployeeId(), timeSheet.getPeriodStart(), timeSheet.getPeriodEnd(),
                    timeSheet.getStatus(), timeSheet.getEntries(), null, null);
        });

        TimeSheetResult result = timeSheetService.create(createRequest(start, end, 1L));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.periodStart()).isEqualTo(start);
        assertThat(result.periodEnd()).isEqualTo(end);
        assertThat(result.status()).isEqualTo(TimeSheetStatus.DRAFT);
        assertThat(result.entries()).isEmpty();
        verify(timeSheetRepository).save(any(TimeSheet.class));
    }

    @Test
    void create_InvalidPeriod_Throws() {
        LocalDate start = LocalDate.of(2026, 1, 31);
        LocalDate end = LocalDate.of(2026, 1, 1);

        assertThatThrownBy(() -> timeSheetService.create(createRequest(start, end, 1L)))
                .isInstanceOf(com.example.backend.timesheet.domain.exception.InvalidTimeSheetPeriodException.class);

        verify(timeSheetRepository, never()).save(any());
    }
}
