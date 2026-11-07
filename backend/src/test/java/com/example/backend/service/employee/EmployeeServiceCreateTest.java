package com.example.backend.service.employee;

import com.example.backend.dto.employee.CreateEmployeeRequest;
import com.example.backend.dto.employee.EmployeeResult;
import com.example.backend.exception.department.DepartmentNotFoundException;
import com.example.backend.exception.employee.EmployeePersonnelNumberAlreadyExistsException;
import com.example.backend.exception.employee.UnknownEmploymentTypeException;
import com.example.backend.exception.position.PositionNotFoundException;
import com.example.backend.model.employee.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceCreateTest extends BaseEmployeeServiceTest {

    @Test
    void create_ValidRequest_Success() {
        stubSuccessCreate("T-001");

        EmployeeResult result = employeeService.create(createRequest("T-001"));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.personnelNumber()).isEqualTo("T-001");
        assertThat(result.surname()).isEqualTo("Иванов");
        assertThat(result.departmentId()).isEqualTo(1L);
        assertThat(result.positionId()).isEqualTo(1L);

        verify(employeeRepository).existsByPersonnelNumber("T-001");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void create_DuplicatePersonnelNumber_ThrowsAndDoesNotSave() {
        when(employeeRepository.existsByPersonnelNumber("T-001")).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(createRequest("T-001")))
                .isInstanceOf(EmployeePersonnelNumberAlreadyExistsException.class)
                .hasMessageContaining("T-001");

        verify(employeeRepository, never()).save(any());
        verifyNoInteractions(departmentRepository, positionRepository);
    }

    @Test
    void create_DepartmentNotFound_ThrowsAndDoesNotSave() {
        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(createRequest("T-001")))
                .isInstanceOf(DepartmentNotFoundException.class);

        verify(employeeRepository, never()).save(any());
        verifyNoInteractions(positionRepository);
    }

    @Test
    void create_PositionNotFound_ThrowsAndDoesNotSave() {
        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department()));
        when(positionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(createRequest("T-001")))
                .isInstanceOf(PositionNotFoundException.class);

        verify(employeeRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "", "  "})
    void create_InvalidEmploymentType_Throws(String type) {
        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(any())).thenReturn(Optional.of(department()));
        when(positionRepository.findById(any())).thenReturn(Optional.of(position()));

        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "T-001", "X", "Y", null, null, null,
                LocalDate.now(), 1L, 1L, type);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(UnknownEmploymentTypeException.class);

        verify(employeeRepository, never()).save(any());
    }

    @ParameterizedTest
    @NullSource
    void create_NullEmploymentType_Throws(String type) {
        when(employeeRepository.existsByPersonnelNumber(any())).thenReturn(false);
        when(departmentRepository.findById(any())).thenReturn(Optional.of(department()));
        when(positionRepository.findById(any())).thenReturn(Optional.of(position()));

        CreateEmployeeRequest request = new CreateEmployeeRequest(
                "T-001", "X", "Y", null, null, null,
                LocalDate.now(), 1L, 1L, type);

        assertThatThrownBy(() -> employeeService.create(request))
                .isInstanceOf(UnknownEmploymentTypeException.class);
    }
}