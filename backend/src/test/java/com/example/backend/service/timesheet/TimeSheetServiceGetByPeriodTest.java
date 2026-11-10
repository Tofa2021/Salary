package com.example.backend.service.timesheet;

import com.example.backend.shared.dto.timesheet.TimeSheetResult;
import com.example.backend.timesheet.exception.TimeSheetNotFoundException;
import com.example.backend.timesheet.model.TimeSheet;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TimeSheetServiceGetByPeriodTest extends BaseTimeSheetServiceTest {
    @Test
    void getByPeriod_Exists_ReturnsResult() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        when(timeSheetRepository.findByDepartmentAndPeriod(1L, 2026, 1))
                .thenReturn(Optional.of(timeSheet));

        TimeSheetResult result = timeSheetService.getByPeriod(2026, 1, 1L);

        assertThat(result.year()).isEqualTo(2026);
        assertThat(result.month()).isEqualTo(1);
    }

    @Test
    void getByPeriod_Approved_ReturnsApprovedTrue() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, true);
        when(timeSheetRepository.findByDepartmentAndPeriod(1L, 2026, 1))
                .thenReturn(Optional.of(timeSheet));

        TimeSheetResult result = timeSheetService.getByPeriod(2026, 1, 1L);

        assertThat(result.approved()).isTrue();
    }

    @Test
    void getByPeriod_NotFound_Throws() {
        when(timeSheetRepository.findByDepartmentAndPeriod(1L, 2026, 1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.getByPeriod(2026, 1, 1L))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }
}