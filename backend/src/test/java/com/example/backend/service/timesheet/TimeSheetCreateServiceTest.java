package com.example.backend.service.timesheet;

import com.example.backend.department.Department;
import com.example.backend.department.exception.DepartmentNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.shared.dto.timesheet.TimeSheetResult;
import com.example.backend.timesheet.exception.TimeSheetAlreadyExistsException;
import com.example.backend.timesheet.model.TimeSheet;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TimeSheetCreateServiceTest extends BaseTimeSheetServiceTest {
    @Test
    void create_ValidRequest_ReturnsTimeSheetWithEntries() {
        Department department = department(1L);
        Employee employee1 = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        Employee employee2 = employee(2L, "T-002", LocalDate.of(2026, 1, 1), null);
        stubCreateSuccess(department, List.of(employee1, employee2));

        TimeSheetResult result = timeSheetService.create(createRequest(2026, 1, 1L));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.year()).isEqualTo(2026);
        assertThat(result.month()).isEqualTo(1);
        assertThat(result.entries()).hasSize(2);
        assertThat(result.approved()).isFalse();
        verify(timeSheetRepository).save(any(TimeSheet.class));
    }

    @Test
    void create_NoEmployees_CreatesEmptyTimeSheet() {
        Department department = department(1L);
        stubCreateSuccess(department, List.of());

        TimeSheetResult result = timeSheetService.create(createRequest(2026, 1, 1L));

        assertThat(result.entries()).isEmpty();
        verify(timeSheetRepository).save(any(TimeSheet.class));
    }

    @Test
    void create_DepartmentNotFound_Throws() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> timeSheetService.create(createRequest(2026, 1, 99L)))
                .isInstanceOf(DepartmentNotFoundException.class);

        verify(timeSheetRepository, never()).save(any());
    }

    @Test
    void create_TimeSheetAlreadyExists_Throws() {
        Department department = department(1L);
        TimeSheet existing = timeSheet(10L, 2026, 1, false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(timeSheetRepository.findByDepartmentAndPeriod(1L, 2026, 1))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> timeSheetService.create(createRequest(2026, 1, 1L)))
                .isInstanceOf(TimeSheetAlreadyExistsException.class);

        verify(timeSheetRepository, never()).save(any());
    }

    @Test
    void create_DismissedBeforePeriod_NotIncluded() {
        Department department = department(1L);
        Employee dismissed = employee(1L, "T-001",
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
        stubCreateSuccess(department, List.of());

        TimeSheetResult result = timeSheetService.create(createRequest(2026, 1, 1L));

        assertThat(result.entries()).isEmpty();
    }

    @Test
    void create_HiredMidPeriod_Included() {
        Department department = department(1L);
        Employee midHired = employee(1L, "T-001", LocalDate.of(2026, 1, 15), null);
        stubCreateSuccess(department, List.of(midHired));

        TimeSheetResult result = timeSheetService.create(createRequest(2026, 1, 1L));

        assertThat(result.entries()).hasSize(1);
    }

    @Test
    void create_NewEntriesHaveZeros() {
        Department department = department(1L);
        Employee employee1 = employee(1L, "T-001", LocalDate.of(2026, 1, 1), null);
        stubCreateSuccess(department, List.of(employee1));

        TimeSheetResult result = timeSheetService.create(createRequest(2026, 1, 1L));

        assertThat(result.entries().getFirst().workedDays()).isZero();
        assertThat(result.entries().getFirst().workedHours()).isZero();
        assertThat(result.entries().getFirst().overtimeHours()).isZero();
    }

    @Test
    void create_NewTimeSheetNotApproved() {
        Department department = department(1L);
        stubCreateSuccess(department, List.of());

        TimeSheetResult result = timeSheetService.create(createRequest(2026, 1, 1L));

        assertThat(result.approved()).isFalse();
        assertThat(result.approvedAt()).isNull();
    }
}
