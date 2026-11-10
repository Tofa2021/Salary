package com.example.backend.service.deduction;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.payroll.exception.deduction.DeductionNotFoundException;
import com.example.backend.payroll.model.TaxDeduction;
import com.example.backend.shared.dto.deduction.DeductionResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TaxDeductionServiceCloseTest extends BaseTaxDeductionServiceTest {

    @Test
    void close_SetsValidToToday() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        DeductionResult result = deductionService.close(10L);

        assertThat(result.validTo()).isEqualTo(LocalDate.now());
    }

    @Test
    void close_AlreadyClosed_OverwritesValidTo() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        deduction.setValidTo(LocalDate.of(2026, 6, 1));
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        DeductionResult result = deductionService.close(10L);

        assertThat(result.validTo()).isEqualTo(LocalDate.now());
    }

    @Test
    void close_NotFound_Throws() {
        when(deductionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deductionService.close(99L)).isInstanceOf(DeductionNotFoundException.class);
    }
}