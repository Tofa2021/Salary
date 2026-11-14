package com.example.backend.service.employmentcontract;

import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNotFoundException;
import com.example.backend.employee.domain.model.ContractType;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentContract;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class EmploymentContractServiceReadTest extends BaseEmploymentContractServiceTest {

    @Test
    void getById_Success() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        EmploymentContractResult result = contractService.getById(10L);

        assertContractResult(result, contract);
    }

    @Test
    void getById_NotFound_Throws() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.getById(99L))
                .isInstanceOf(EmploymentContractNotFoundException.class);
    }

    @Test
    void findByEmployee_Success() {
        Employee employee = employee(1L, "T-001");
        List<EmploymentContract> contracts = List.of(
                contract(10L, employee),
                fixedTermContract(11L, employee, LocalDate.of(2026, 12, 31))
        );
        when(contractRepository.findByEmployeeId(1L)).thenReturn(contracts);

        List<EmploymentContractResult> result = contractService.findByEmployee(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).contractNumber()).isEqualTo("C-10");
        assertThat(result.get(1).contractNumber()).isEqualTo("C-11");
    }

    @Test
    void findByEmployee_Empty_ReturnsEmpty() {
        when(contractRepository.findByEmployeeId(1L)).thenReturn(List.of());

        assertThat(contractService.findByEmployee(1L)).isEmpty();
    }

    @Test
    void findActiveMainOnDate_Success() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        LocalDate date = LocalDate.of(2026, 6, 1);
        when(contractRepository.findActiveMainOnDate(1L, date)).thenReturn(Optional.of(contract));

        EmploymentContractResult result = contractService.findActiveMainOnDate(1L, date);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.active()).isTrue();
    }

    @Test
    void findActiveMainOnDate_NoActive_Throws() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        when(contractRepository.findActiveMainOnDate(1L, date)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.findActiveMainOnDate(1L, date))
                .isInstanceOf(EmploymentContractNotFoundException.class);
    }

    @Test
    void findExpiring_ReturnsFixedTermContracts() {
        Employee employee = employee(1L, "T-001");
        List<EmploymentContract> contracts = List.of(
                fixedTermContract(10L, employee, TODAY.plusDays(15))
        );
        when(contractRepository.findExpiring(
                ContractType.FIXED_TERM,
                TODAY,
                TODAY.plusDays(30))).thenReturn(contracts);

        List<EmploymentContractResult> result = contractService.findExpiring(30);

        assertThat(result).hasSize(1);
    }
}
