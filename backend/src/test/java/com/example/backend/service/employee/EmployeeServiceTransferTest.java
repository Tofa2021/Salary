package com.example.backend.service.employee;

import com.example.backend.department.Department;
import com.example.backend.department.exception.DepartmentNotFoundException;
import com.example.backend.employee.domain.exception.employee.EmployeeDismissedException;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.exception.employee.SameDepartmentException;
import com.example.backend.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class EmployeeServiceTransferTest extends BaseEmployeeServiceTest {

    @Test
    void transfer_Success() {
        Employee employee = employee(1L, "T-001");
        Department newDept = department(2L, "HR");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDept));

        employeeService.transfer(2L, 1L);

        assertThat(employee.getDepartment()).isSameAs(newDept);
    }

    @Test
    void transfer_EmployeeDismissed_Throws() {
        Employee employee = employee(1L, "T-001");
        employee.setDismissalDate(LocalDate.of(2025, 6, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.transfer(2L, 1L))
                .isInstanceOf(EmployeeDismissedException.class);

        verifyNoInteractions(departmentRepository);
    }

    @Test
    void transfer_DepartmentNotFound_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.transfer(99L, 1L))
                .isInstanceOf(DepartmentNotFoundException.class);
    }

    @Test
    void transfer_SameDepartment_Throws() {
        Employee employee = employee(1L, "T-001");   // department = 1L
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department()));

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