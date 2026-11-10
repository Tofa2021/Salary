package com.example.backend.service.employee;

import com.example.backend.employee.domain.exception.employee.DismissalDateBeforeHireDateException;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.shared.dto.employee.DismissRequest;
import com.example.backend.shared.dto.employee.EmployeeResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceDismissTest extends BaseEmployeeServiceTest {

    @Test
    void dismiss_Success() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResult result = employeeService.dismiss(1L,
                new DismissRequest(LocalDate.of(2026, 6, 1)));

        assertThat(result.dismissalDate()).isEqualTo(LocalDate.of(2026, 6, 1));
    }

    @Test
    void dismiss_BeforeHireDate_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.dismiss(1L,
                new DismissRequest(LocalDate.of(2025, 12, 31))))
                .isInstanceOf(DismissalDateBeforeHireDateException.class);

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void dismiss_NotFound_Throws() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.dismiss(99L, new DismissRequest(LocalDate.now())))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}