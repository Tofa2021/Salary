package com.example.backend.service.deduction;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.payroll.exception.deduction.DeductionNotFoundException;
import com.example.backend.payroll.model.TaxDeduction;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaxDeductionServiceDeleteTest extends BaseTaxDeductionServiceTest {

    @Test
    void delete_Success() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        deductionService.delete(10L);

        verify(deductionRepository).delete(deduction);
    }

    @Test
    void delete_NotFound_Throws() {
        when(deductionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deductionService.delete(99L)).isInstanceOf(DeductionNotFoundException.class);
        verify(deductionRepository, never()).delete(any());
    }
}