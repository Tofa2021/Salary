package com.example.backend.service.timesheet;

import com.example.backend.shared.dto.timesheet.TimeSheetSummaryResult;
import com.example.backend.timesheet.model.TimeSheet;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TimeSheetServiceGetAllTest extends BaseTimeSheetServiceTest {
    @Test
    void getAll_MultipleTimeSheets_ReturnsList() {
        List<TimeSheet> timeSheets = List.of(
                timeSheet(1L, 2026, 1, false),
                timeSheet(2L, 2026, 1, true)
        );
        when(timeSheetRepository.findAllByYearAndMonth(2026, 1)).thenReturn(timeSheets);

        List<TimeSheetSummaryResult> result = timeSheetService.getAll(2026, 1);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAll_Empty_ReturnsEmptyList() {
        when(timeSheetRepository.findAllByYearAndMonth(2026, 1)).thenReturn(List.of());

        assertThat(timeSheetService.getAll(2026, 1)).isEmpty();
    }
}