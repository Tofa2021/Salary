package com.example.backend.service.employmentcontract;

import com.example.backend.dto.employmentcontract.EmploymentContractResult;
import com.example.backend.exception.employmentcontract.EmploymentContractNotFoundException;
import com.example.backend.exception.employmentcontract.InvalidEmploymentContractPeriodException;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.employee.EmploymentContract;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class EmploymentContractServiceExtendTest extends BaseEmploymentContractServiceTest {

    @Test
    void extend_fixedTerm_success_setsNewEndDate() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = fixedTermContract(10L, employee, LocalDate.of(2026, 6, 1));
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        LocalDate newEnd = LocalDate.of(2026, 12, 31);
        EmploymentContractResult result = contractService.extend(10L, newEnd);

        assertThat(result.endDate()).isEqualTo(newEnd);
    }

    @Test
    void extend_indefinite_throws() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        assertThatThrownBy(() -> contractService.extend(10L, LocalDate.of(2026, 12, 31)))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void extend_newEndDateBeforeStart_throws() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = fixedTermContract(10L, employee, LocalDate.of(2026, 6, 1));
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        assertThatThrownBy(() -> contractService.extend(10L, LocalDate.of(2025, 12, 31)))
                .isInstanceOf(InvalidEmploymentContractPeriodException.class);
    }

    @Test
    void extend_notFound_throws() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.extend(99L, LocalDate.now()))
                .isInstanceOf(EmploymentContractNotFoundException.class);
    }
}