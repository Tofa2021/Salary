package com.example.backend.service.timesheet;

import com.example.backend.exception.timesheet.TimeSheetNotFoundException;
import com.example.backend.model.timesheet.TimeSheet;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TimeSheetServiceIsApprovedTest extends BaseTimeSheetServiceTest {

    @Test
    void isApproved_True() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, true);
        when(timeSheetRepository.findByDepartmentAndPeriod(1L, 2026, 1))
                .thenReturn(Optional.of(timeSheet));

        assertThat(timeSheetService.isApproved(2026, 1, 1L)).isTrue();
    }

    @Test
    void isApproved_False() {
        TimeSheet timeSheet = timeSheet(1L, 2026, 1, false);
        when(timeSheetRepository.findByDepartmentAndPeriod(1L, 2026, 1))
                .thenReturn(Optional.of(timeSheet));

        assertThat(timeSheetService.isApproved(2026, 1, 1L)).isFalse();
    }

    @Test
    void isApproved_NotFound_Throws() {
        when(timeSheetRepository.findByDepartmentAndPeriod(1L, 2026, 1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.isApproved(2026, 1, 1L))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }
}
