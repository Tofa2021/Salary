package com.example.backend.service.deduction;

import com.example.backend.dto.deduction.DeductionResult;
import com.example.backend.dto.deduction.UpdateDeductionRequest;
import com.example.backend.exception.deduction.DeductionNotFoundException;
import com.example.backend.exception.deduction.InvalidDeductionPeriodException;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.payroll.TaxDeduction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

class TaxDeductionServiceUpdateTest extends BaseTaxDeductionServiceTest {

    @Test
    void update_AllFields_UpdatesEverything() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        UpdateDeductionRequest request = new UpdateDeductionRequest(
                BigDecimal.valueOf(300), LocalDate.of(2027, 1, 1), "NEW-DOC");

        DeductionResult result = deductionService.update(10L, request);

        assertThat(result.amount()).isEqualByComparingTo("300");
        assertThat(result.validTo()).isEqualTo(LocalDate.of(2027, 1, 1));
        assertThat(result.documentNumber()).isEqualTo("NEW-DOC");
    }

    @Test
    void update_AllNullFields_DoesNotChangeAnything() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        UpdateDeductionRequest request = new UpdateDeductionRequest(null, null, null);

        DeductionResult result = deductionService.update(10L, request);

        assertThat(result.amount()).isEqualByComparingTo("200");
        assertThat(result.validTo()).isNull();
        assertThat(result.documentNumber()).isEqualTo("DOC-10");
    }

    @Test
    void update_OnlyAmountChanged() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        UpdateDeductionRequest request = new UpdateDeductionRequest(BigDecimal.valueOf(250), null, null);

        DeductionResult result = deductionService.update(10L, request);

        assertThat(result.amount()).isEqualByComparingTo("250");
        assertThat(result.validTo()).isNull();
    }

    @Test
    void update_ValidToBeforeValidFrom_Throws() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        UpdateDeductionRequest request = new UpdateDeductionRequest(
                null, LocalDate.of(2025, 12, 31), null);

        assertThatThrownBy(() -> deductionService.update(10L, request)).isInstanceOf(InvalidDeductionPeriodException.class);
    }

    @Test
    void update_NotFound_Throws() {
        when(deductionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deductionService.update(99L,
                new UpdateDeductionRequest(BigDecimal.valueOf(100), null, null)))
                .isInstanceOf(DeductionNotFoundException.class);
    }
}
