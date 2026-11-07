package com.example.backend.service.employee;

import com.example.backend.exception.employee.EmployeeDismissedException;
import com.example.backend.exception.employee.EmployeeNotFoundException;
import com.example.backend.exception.employee.EmployeeWithoutDepartmentException;
import com.example.backend.model.employee.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class EmployeeServiceRemoveFromDepartmentTest extends BaseEmployeeServiceTest {

    @Test
    void removeFromDepartment_Success() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.removeFromDepartment(1L);

        assertThat(employee.getDepartment()).isNull();
    }

    @Test
    void removeFromDepartment_EmployeeDismissed_Throws() {
        Employee employee = employee(1L, "T-001");
        employee.setDismissalDate(LocalDate.of(2025, 6, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.removeFromDepartment(1L))
                .isInstanceOf(EmployeeDismissedException.class);
    }

    @Test
    void removeFromDepartment_WithoutDepartment_Throws() {
        Employee employee = employee(1L, "T-001");
        employee.setDepartment(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.removeFromDepartment(1L))
                .isInstanceOf(EmployeeWithoutDepartmentException.class);
    }

    @Test
    void removeFromDepartment_NotFound_Throws() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.removeFromDepartment(99L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}