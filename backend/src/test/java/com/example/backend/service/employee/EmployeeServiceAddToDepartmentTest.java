package com.example.backend.service.employee;

import com.example.backend.exception.department.DepartmentNotFoundException;
import com.example.backend.exception.employee.EmployeeAlreadyInDepartmentException;
import com.example.backend.exception.employee.EmployeeDismissedException;
import com.example.backend.model.employee.Department;
import com.example.backend.model.employee.Employee;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class EmployeeServiceAddToDepartmentTest extends BaseEmployeeServiceTest {

    @Test
    void addToDepartment_Success() {
        Employee employee = employee(1L, "T-001");
        employee.setDepartment(null);
        Department newDepartment = department(2L, "HR");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(2L)).thenReturn(Optional.of(newDepartment));

        employeeService.addToDepartment(2L, 1L);

        assertThat(employee.getDepartment()).isSameAs(newDepartment);
    }

    @Test
    void addToDepartment_EmployeeDismissed_Throws() {
        Employee employee = employee(1L, "T-001");
        employee.setDepartment(null);
        employee.setDismissalDate(LocalDate.of(2025, 6, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.addToDepartment(2L, 1L))
                .isInstanceOf(EmployeeDismissedException.class);
    }

    @Test
    void addToDepartment_AlreadyInDepartment_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> employeeService.addToDepartment(2L, employee.getId()))
                .isInstanceOf(EmployeeAlreadyInDepartmentException.class);
    }

    @Test
    void addToDepartment_departmentNotFound_throws() {
        Employee employee = employee(1L, "T-001");
        employee.setDepartment(null);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.addToDepartment(99L, 1L))
                .isInstanceOf(DepartmentNotFoundException.class);
    }
}