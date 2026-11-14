package com.example.backend.service.employmentcontract;

import com.example.backend.employee.api.dto.EmploymentContractResult;
import com.example.backend.employee.api.dto.UpdateEmploymentContractCommand;
import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentContract;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class EmploymentContractServiceUpdateTest extends BaseEmploymentContractServiceTest {

    @Test
    void update_allFields_updatesEverything() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        var command = new UpdateEmploymentContractCommand(
                BigDecimal.valueOf(2000), null, BigDecimal.valueOf(25), 20);

        EmploymentContractResult result = contractService.update(10L, command);

        assertThat(result.baseSalary()).isEqualByComparingTo("2000");
        assertThat(result.bonusPercent()).isEqualByComparingTo("25");
        assertThat(result.workHoursPerWeek()).isEqualTo(20);
    }

    @Test
    void update_allNullFields_doesNotChangeAnything() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        var command = new UpdateEmploymentContractCommand(null, null, null, null);

        EmploymentContractResult result = contractService.update(10L, command);

        assertThat(result.baseSalary()).isEqualByComparingTo("1000");
        assertThat(result.workHoursPerWeek()).isEqualTo(40);
    }

    @Test
    void update_onlySalaryChanged() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        var command = new UpdateEmploymentContractCommand(
                BigDecimal.valueOf(2500), null, null, null);

        EmploymentContractResult result = contractService.update(10L, command);

        assertThat(result.baseSalary()).isEqualByComparingTo("2500");
        assertThat(result.workHoursPerWeek()).isEqualTo(40);
    }

    @Test
    void update_notFound_throws() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.update(99L,
                new UpdateEmploymentContractCommand(null, null, null, null)))
                .isInstanceOf(EmploymentContractNotFoundException.class);
    }
}
