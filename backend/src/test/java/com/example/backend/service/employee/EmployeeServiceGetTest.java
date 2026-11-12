package com.example.backend.service.employee;

import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceGetTest extends BaseEmployeeServiceTest {

    @Test
    void getById_Success() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResult result = employeeService.getById(1L);

        assertEmployeeResult(result, employee);
    }

    @Test
    void getById_NotFound_Throws() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(1L))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void getByPersonnelNumber_Success() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findByPersonnelNumber("T-001")).thenReturn(Optional.of(employee));

        EmployeeResult result = employeeService.getByPersonnelNumber("T-001");

        assertEmployeeResult(result, employee);
    }

    @Test
    void getByPersonnelNumber_NotFound_Throws() {
        when(employeeRepository.findByPersonnelNumber("T-001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getByPersonnelNumber("T-001"))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("T-001");
    }

    @Test
    void getAll_Success_KeepsPaginationMetadata() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Employee> employees = List.of(employee(1L, "T-001"), employee(2L, "T-002"));
        Page<Employee> springPage = new PageImpl<>(employees, pageable, 57);
        when(employeeRepository.findAll(pageable)).thenReturn(springPage);

        Page<EmployeeResult> result = employeeService.getAll(pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(57);
        assertThat(result.getTotalPages()).isEqualTo(29);
        verify(employeeRepository).findAll(pageable);
    }

    @Test
    void getAll_PageBeyondRange_ReturnsEmptyWithMetadata() {
        Pageable pageable = PageRequest.of(100, 20);
        when(employeeRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(), pageable, 25));

        Page<EmployeeResult> result = employeeService.getAll(pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(25);
    }

    @Test
    void getByDepartment_Success() {
        Pageable pageable = PageRequest.of(0, 2);
        List<Employee> employees = List.of(employee(1L, "T-001"), employee(2L, "T-002"));
        Page<Employee> springPage = new PageImpl<>(employees, pageable, 2);
        when(employeeRepository.findByDepartmentId(1L, pageable)).thenReturn(springPage);

        Page<EmployeeResult> result = employeeService.getByDepartment(1L, pageable);

        assertThat(result.getContent()).hasSize(2);
    }
}