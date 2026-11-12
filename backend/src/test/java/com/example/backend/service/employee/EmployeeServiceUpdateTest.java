package com.example.backend.service.employee;

import com.example.backend.employee.api.dto.EmployeeResult;
import com.example.backend.employee.domain.exception.employee.EmployeeInvalidDepartmentException;
import com.example.backend.employee.domain.exception.employee.EmployeeInvalidPositionException;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.exception.employee.UnknownEmploymentTypeException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.infrastructure.web.dto.UpdateEmployeeRequest;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class EmployeeServiceUpdateTest extends BaseEmployeeServiceTest {

    @Test
    void update_AllFields_UpdatesEverything() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentLookup.findById(1L)).thenReturn(Optional.of(departmentResult()));
        when(positionLookup.findById(1L)).thenReturn(Optional.of(positionResult()));

        EmployeeResult result = employeeService.update(1L, updateRequest());

        assertThat(result.surname()).isEqualTo("A");
        assertThat(result.name()).isEqualTo("B");
        assertThat(result.middleName()).isEqualTo("C");
        assertThat(result.email()).isEqualTo("asd@gmail.com");
    }

    @Test
    void update_AllNullFields_DoesNotChangeAnything() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        UpdateEmployeeRequest request = new UpdateEmployeeRequest(
                null, null, null, null, null, null, null, null);

        EmployeeResult result = employeeService.update(1L, request);

        assertThat(result.surname()).isEqualTo("Иванов");
        assertThat(result.name()).isEqualTo("Иван");
    }

    @Test
    void update_OnlyNameChanged_OtherFieldsUnchanged() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        UpdateEmployeeRequest request = new UpdateEmployeeRequest(
                null, "НовоеИмя", null, null, null, null, null, null);

        EmployeeResult result = employeeService.update(1L, request);

        assertThat(result.name()).isEqualTo("НовоеИмя");
        assertThat(result.surname()).isEqualTo("Иванов");
    }

    @Test
    void update_DepartmentNotFound_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentLookup.findById(99L)).thenReturn(Optional.empty());

        UpdateEmployeeRequest request = new UpdateEmployeeRequest(
                null, null, null, null, null, 99L, null, null);

        assertThatThrownBy(() -> employeeService.update(1L, request))
                .isInstanceOf(EmployeeInvalidDepartmentException.class);
    }

    @Test
    void update_PositionNotFound_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(positionLookup.findById(99L)).thenReturn(Optional.empty());

        UpdateEmployeeRequest request = new UpdateEmployeeRequest(
                null, null, null, null, null, null, 99L, null);

        assertThatThrownBy(() -> employeeService.update(1L, request))
                .isInstanceOf(EmployeeInvalidPositionException.class);
    }

    @Test
    void update_InvalidEmploymentType_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        UpdateEmployeeRequest request = new UpdateEmployeeRequest(
                null, null, null, null, null, null, null, "INVALID");

        assertThatThrownBy(() -> employeeService.update(1L, request))
                .isInstanceOf(UnknownEmploymentTypeException.class);
    }

    @Test
    void update_NotFound_Throws() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(99L, updateRequest()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }
}