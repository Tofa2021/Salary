package com.example.backend.service.employmentcontract;

import com.example.backend.employee.domain.exception.employmentcontract.EmploymentContractNotFoundException;
import com.example.backend.employee.domain.model.Employee;
import com.example.backend.employee.domain.model.EmploymentContract;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmploymentContractServiceDeleteTest extends BaseEmploymentContractServiceTest {

    @Test
    void delete_success() {
        Employee employee = employee(1L, "T-001");
        EmploymentContract contract = contract(10L, employee);
        when(contractRepository.findById(10L)).thenReturn(Optional.of(contract));

        contractService.delete(10L);

        verify(contractRepository).delete(contract);
    }

    @Test
    void delete_notFound_throws() {
        when(contractRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.delete(99L))
                .isInstanceOf(EmploymentContractNotFoundException.class);

        verify(contractRepository, never()).delete(any());
    }
}
