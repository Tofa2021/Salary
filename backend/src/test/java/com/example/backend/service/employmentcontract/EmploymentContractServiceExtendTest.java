package com.example.backend.service.employmentcontract;

import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNotFoundException;
import com.example.backend.employee.domain.exception.employmentcontract.ExtendEmploymentContractException;
import com.example.backend.employee.domain.exception.employmentcontract.InvalidEmploymentContractPeriodException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentContract;
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
                .isInstanceOf(ExtendEmploymentContractException.class);
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
