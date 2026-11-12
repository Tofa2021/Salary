package com.example.backend.service.employee;

import com.example.backend.employee.domain.exception.employee.EmployeeAlreadyInDepartmentException;
import com.example.backend.employee.domain.exception.employee.EmployeeDismissedException;
import com.example.backend.employee.domain.exception.employee.EmployeeInvalidDepartmentException;
import com.example.backend.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceAddToDepartmentTest extends BaseEmployeeServiceTest {

    @Test
    void addToDepartment_Success() {
        Employee employee = employee(1L, "T-001", null, null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentLookup.findById(2L)).thenReturn(Optional.of(departmentResult(2L, "HR")));

        employeeService.addToDepartment(2L, 1L);

        assertThat(employee.getDepartmentId()).isEqualTo(2L);
        verify(employeeRepository).save(employee);
    }

    @Test
    void addToDepartment_EmployeeDismissed_Throws() {
        Employee employee = employee(1L, "T-001", LocalDate.of(2025, 6, 1), null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.addToDepartment(2L, 1L))
                .isInstanceOf(EmployeeDismissedException.class);
    }

    @Test
    void addToDepartment_AlreadyInDepartment_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.addToDepartment(2L, 1L))
                .isInstanceOf(EmployeeAlreadyInDepartmentException.class);
    }

    @Test
    void addToDepartment_departmentNotFound_throws() {
        Employee employee = employee(1L, "T-001", null, null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentLookup.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.addToDepartment(99L, 1L))
                .isInstanceOf(EmployeeInvalidDepartmentException.class);
    }
}
