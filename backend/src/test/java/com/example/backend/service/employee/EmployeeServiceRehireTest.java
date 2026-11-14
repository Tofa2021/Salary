package com.example.backend.service.employee;

import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceRehireTest extends BaseEmployeeServiceTest {

    @Test
    void rehire_Success_ClearsDismissalDate() {
        Employee employee = employee(1L, "T-001", LocalDate.of(2025, 6, 1), 1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResult result = employeeService.rehire(1L, LocalDate.of(2026, 1, 1));

        assertThat(result.hireDate()).isEqualTo(LocalDate.of(2026, 1, 1));
        assertThat(result.dismissalDate()).isNull();
        verify(employeeRepository).save(employee);
    }

    @Test
    void rehire_NotFound_Throws() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.rehire(99L, LocalDate.now()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}
