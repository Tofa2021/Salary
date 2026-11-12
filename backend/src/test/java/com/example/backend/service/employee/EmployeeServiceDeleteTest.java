package com.example.backend.service.employee;

import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceDeleteTest extends BaseEmployeeServiceTest {

    @Test
    void delete_Success() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.delete(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    void delete_NotFound_Throws() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.delete(99L))
                .isInstanceOf(EmployeeNotFoundException.class);

        verify(employeeRepository, never()).delete(any());
    }
}
