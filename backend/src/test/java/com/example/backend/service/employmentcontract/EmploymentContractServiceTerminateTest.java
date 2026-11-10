package com.example.backend.service.employmentcontract;

import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractAlreadyTerminatedException;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNotFoundException;
import com.example.backend.employee.domain.exception.employmentcontract.InvalidEmploymentContractPeriodException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentContract;
import com.example.backend.shared.dto.employmentcontract.EmploymentContractResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class EmploymentContractServiceTerminateTest extends BaseEmploymentContractServiceTest {

    @Test
    void terminate_success_setsEndDate() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        LocalDate termination = LocalDate.of(2026, 6, 1);
        EmploymentContractResult result = contractService.terminate(10L, termination);

        assertThat(result.endDate()).isEqualTo(termination);
    }

    @Test
    void terminate_alreadyTerminated_throws() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        contract.setEndDate(LocalDate.now().minusDays(1));   // уже истёк
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        assertThatThrownBy(() -> contractService.terminate(10L, LocalDate.now()))
                .isInstanceOf(EmploymentContractAlreadyTerminatedException.class);
    }

    @Test
    void terminate_beforeStartDate_throws() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);   // startDate = 2026-01-01
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        assertThatThrownBy(() -> contractService.terminate(10L, LocalDate.of(2025, 12, 31)))
                .isInstanceOf(InvalidEmploymentContractPeriodException.class);
    }

    @Test
    void terminate_notFound_throws() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.terminate(99L, LocalDate.now()))
                .isInstanceOf(EmploymentContractNotFoundException.class);
    }
}