package com.example.backend.service.employee;

import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.shared.pagination.PageRequest;
import com.example.backend.shared.pagination.PageResult;
import org.junit.jupiter.api.Test;

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
        PageRequest request = PageRequest.of(0, 2);
        List<Employee> employees = List.of(employee(1L, "T-001"), employee(2L, "T-002"));
        PageResult<Employee> page = PageResult.of(employees, 0, 2, 57);
        when(employeeRepository.findAll(request)).thenReturn(page);

        PageResult<EmployeeResult> result = employeeService.getAll(request);

        assertThat(result.items()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(57);
        assertThat(result.totalPages()).isEqualTo(29);
        verify(employeeRepository).findAll(request);
    }

    @Test
    void getAll_PageBeyondRange_ReturnsEmptyWithMetadata() {
        PageRequest request = PageRequest.of(100, 20);
        when(employeeRepository.findAll(request))
                .thenReturn(PageResult.empty(100, 20));

        PageResult<EmployeeResult> result = employeeService.getAll(request);

        assertThat(result.items()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    void getByDepartment_Success() {
        PageRequest request = PageRequest.of(0, 2);
        List<Employee> employees = List.of(employee(1L, "T-001"), employee(2L, "T-002"));
        PageResult<Employee> page = PageResult.of(employees, 0, 2, 2);
        when(employeeRepository.findByDepartmentId(1L, request)).thenReturn(page);

        PageResult<EmployeeResult> result = employeeService.getByDepartment(1L, request);

        assertThat(result.items()).hasSize(2);
    }
}
