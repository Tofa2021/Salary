package com.example.backend.service.timesheet;

import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import com.example.backend.timesheet.api.dto.TimeSheetResult;
import com.example.backend.timesheet.api.dto.TimeSheetSummaryResult;
import com.example.backend.timesheet.domain.exception.TimeSheetNotFoundException;
import com.example.backend.timesheet.domain.model.TimeSheet;
import com.example.backend.timesheet.domain.model.TimeSheetEntryType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TimeSheetServiceGetTest extends BaseTimeSheetServiceTest {
    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 1, 31);

    @Test
    void getById_Exists_ReturnsResult() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        timeSheet.getEntries().add(entry(10L, LocalDate.of(2026, 1, 15), 8, TimeSheetEntryType.REGULAR));
        timeSheet.getEntries().add(entry(11L, LocalDate.of(2026, 1, 16), 2, TimeSheetEntryType.OVERTIME));
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        TimeSheetResult result = timeSheetService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.entries()).hasSize(2);
        assertThat(result.totalHours()).isEqualByComparingTo("10");
        assertThat(result.hoursByType().get(TimeSheetEntryType.REGULAR)).isEqualByComparingTo("8");
        assertThat(result.hoursByType().get(TimeSheetEntryType.OVERTIME)).isEqualByComparingTo("2");
    }

    @Test
    void getById_NotFound_Throws() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.getById(99L))
                .isInstanceOf(TimeSheetNotFoundException.class);
    }

    @Test
    void findByIdOptional_Present() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findById(1L)).thenReturn(Optional.of(timeSheet));

        assertThat(timeSheetService.findByIdOptional(1L)).isPresent();
    }

    @Test
    void findByIdOptional_Empty() {
        when(timeSheetRepository.findById(99L)).thenReturn(Optional.empty());

        assertThat(timeSheetService.findByIdOptional(99L)).isEmpty();
    }

    @Test
    void getByEmployee_ReturnsPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageResult<TimeSheet> page = PageResult.of(List.of(timeSheet(1L, START, END)), 0, 10, 1);
        when(timeSheetRepository.findByEmployeeId(1L, pageRequest)).thenReturn(page);

        PageResult<TimeSheetResult> result = timeSheetService.getByEmployee(1L, pageRequest);

        assertThat(result.items()).hasSize(1);
    }

    @Test
    void getByPeriod_ReturnsPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageResult<TimeSheet> page = PageResult.of(List.of(timeSheet(1L, START, END)), 0, 10, 1);
        when(timeSheetRepository.findByPeriod(START, END, pageRequest)).thenReturn(page);

        PageResult<TimeSheetResult> result = timeSheetService.getByPeriod(START, END, pageRequest);

        assertThat(result.items()).hasSize(1);
    }

    @Test
    void findHoursSummaryOptional_Present() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        timeSheet.getEntries().add(entry(10L, LocalDate.of(2026, 1, 15), 8, TimeSheetEntryType.REGULAR));
        when(timeSheetRepository.findByEmployeeIdAndPeriod(1L, START, END)).thenReturn(Optional.of(timeSheet));

        Optional<TimeSheetSummaryResult> result = timeSheetService.findHoursSummaryOptional(1L, START, END);

        assertThat(result).isPresent();
        assertThat(result.get().totalHours()).isEqualByComparingTo("8");
    }

    @Test
    void findHoursSummaries_ReturnsList() {
        TimeSheet timeSheet = timeSheet(1L, START, END);
        when(timeSheetRepository.findAllByPeriod(START, END)).thenReturn(List.of(timeSheet));

        List<TimeSheetSummaryResult> result = timeSheetService.findHoursSummaries(START, END);

        assertThat(result).hasSize(1);
    }
}
