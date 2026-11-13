package com.example.backend.service.employmentcontract;

import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.domain.exception.employee.EmployeeNotFoundException;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNumberAlreadyExistsException;
import com.example.backend.employee.domain.exception.employmentcontract.InvalidEmploymentContractPeriodException;
import com.example.backend.employee.domain.exception.employmentcontract.MainJobAlreadyExistsException;
import com.example.backend.employee.domain.exception.employmentcontract.UnknownEmploymentContractTypeException;
import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentContract;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class EmploymentContractServiceCreateTest extends BaseEmploymentContractServiceTest {

    @Test
    void create_Success() {
        Employee employee = employee(1L, "T-001");
        stubCreateSuccess(employee);

        EmploymentContractResult result = contractService.create(
                1L, createCommand("C-001", "INDEFINITE", true));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.contractNumber()).isEqualTo("C-001");
        assertThat(result.contractType()).isEqualTo(ContractType.INDEFINITE);
        assertThat(result.mainJob()).isTrue();
        assertThat(result.active()).isTrue();

        verify(contractRepository).existsByContractNumber("C-001");
        verify(contractRepository).save(any(EmploymentContract.class));
    }

    @Test
    void create_EmployeeNotFound_Throws() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> contractService.create(99L,
                createCommand("C-001", "INDEFINITE", true)))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining("99");

        verify(contractRepository, never()).save(any());
    }

    @Test
    void create_DuplicateContractNumber_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.existsByContractNumber("C-001")).thenReturn(true);

        assertThatThrownBy(() -> contractService.create(1L,
                createCommand("C-001", "INDEFINITE", true)))
                .isInstanceOf(EmploymentContractNumberAlreadyExistsException.class);

        verify(contractRepository, never()).save(any());
    }

    @Test
    void create_EndDateBeforeStartDate_Throws() {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.existsByContractNumber(any())).thenReturn(false);

        var request = createCommand(
                "C-001", "FIXED_TERM",
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 1, 1),
                true);

        assertThatThrownBy(() -> contractService.create(1L, request))
                .isInstanceOf(InvalidEmploymentContractPeriodException.class);

        verify(contractRepository, never()).save(any());
    }

    @Test
    void create_MainJobAlreadyExists_Throws() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract existing = contract(10L, employee);
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.existsByContractNumber(any())).thenReturn(false);
        when(contractRepository.findOverlappingMainJob(eq(1L), any(), any(), any()))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> contractService.create(1L,
                createCommand("C-001", "INDEFINITE", true)))
                .isInstanceOf(MainJobAlreadyExistsException.class);

        verify(contractRepository, never()).save(any());
    }

    @Test
    void create_NotMainJob_DoesNotCheckExistingMain() {
        Employee employee = employee(1L, "T-001");
        stubCreateBase(employee);

        contractService.create(1L, createCommand("C-001", "CONTRACT", false));

        verify(contractRepository, never())
                .findOverlappingMainJob(any(), any(), any(), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "", "  "})
    void create_InvalidContractType_Throws(String type) {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.existsByContractNumber(any())).thenReturn(false);

        assertThatThrownBy(() -> contractService.create(1L,
                createCommand("C-001", type, true)))
                .isInstanceOf(UnknownEmploymentContractTypeException.class);

        verify(contractRepository, never()).save(any());
    }

    @ParameterizedTest
    @NullSource
    void create_NullContractType_Throws(String type) {
        Employee employee = employee(1L, "T-001");
        when(employeeRepository.existsById(1L)).thenReturn(true);
        when(contractRepository.existsByContractNumber(any())).thenReturn(false);

        assertThatThrownBy(() -> contractService.create(1L,
                createCommand("C-001", type, true)))
                .isInstanceOf(UnknownEmploymentContractTypeException.class);
    }
}
