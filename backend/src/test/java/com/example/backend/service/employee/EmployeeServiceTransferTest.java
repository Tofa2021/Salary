package com.example.backend.service.employee;

import com.example.backend.employee.domain.exception.employee.*;
import com.example.backend.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class EmployeeServiceTransferTest extends BaseEmployeeServiceTest {

    @Test
    void transfer_Success() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentLookup.findById(2L)).thenReturn(Optional.of(departmentResult(2L, "HR")));

        employeeService.transfer(2L, 1L);

        assertThat(employee.getDepartmentId()).isEqualTo(2L);
        verify(employeeRepository).save(employee);
    }

    @Test
    void transfer_EmployeeDismissed_Throws() {
        Employee employee = employee(1L, "T-001");
        employee.setDismissalDate(LocalDate.of(2025, 6, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.transfer(2L, 1L))
                .isInstanceOf(EmployeeDismissedException.class);

        verifyNoInteractions(departmentLookup);
    }

    @Test
    void transfer_EmployeeWithoutDepartment_Throws() {
        Employee employee = employee(1L, "T-001");
        employee.setDepartmentId(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.transfer(2L, 1L))
                .isInstanceOf(EmployeeWithoutDepartmentException.class);

        verifyNoInteractions(departmentLookup);
    }

    @Test
    void transfer_DepartmentNotFound_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentLookup.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.transfer(99L, 1L))
                .isInstanceOf(EmployeeInvalidDepartmentException.class);
    }

    @Test
    void transfer_SameDepartment_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentLookup.findById(1L)).thenReturn(Optional.of(departmentResult(1L, "IT")));

        assertThatThrownBy(() -> employeeService.transfer(1L, 1L))
                .isInstanceOf(SameDepartmentException.class);
    }

    @Test
    void transfer_NotFound_Throws() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.transfer(2L, 99L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}
