package com.example.backend.service.deduction;

import com.example.backend.dto.deduction.DeductionResult;
import com.example.backend.exception.deduction.DeductionNotFoundException;
import com.example.backend.model.employee.Employee;
import com.example.backend.model.payroll.DeductionType;
import com.example.backend.model.payroll.TaxDeduction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaxDeductionServiceReadTest extends BaseTaxDeductionServiceTest {

    @Test
    void getById_Success() {
        Employee employee = employee(1L);
        TaxDeduction deduction = standardSelfDeduction(10L, employee);
        when(deductionRepository.findById(10L)).thenReturn(Optional.of(deduction));

        DeductionResult result = deductionService.getById(10L);

        assertDeductionResult(result, deduction);
    }

    @Test
    void getById_NotFound_Throws() {
        when(deductionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> deductionService.getById(99L)).isInstanceOf(DeductionNotFoundException.class);
    }

    @Test
    void findByEmployee_ReturnsAll() {
        Employee employee = employee(1L);
        List<TaxDeduction> list = List.of(
                standardSelfDeduction(10L, employee),
                childDeduction(11L, employee, "DOC-CHILD")
        );
        when(deductionRepository.findByEmployeeId(1L)).thenReturn(list);

        List<DeductionResult> result = deductionService.findByEmployee(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).type()).isEqualTo(DeductionType.STANDARD_SELF);
        assertThat(result.get(1).type()).isEqualTo(DeductionType.CHILD);
    }

    @Test
    void findByEmployee_Empty_ReturnsEmpty() {
        when(deductionRepository.findByEmployeeId(1L)).thenReturn(List.of());

        assertThat(deductionService.findByEmployee(1L)).isEmpty();
    }

    @Test
    void findActiveOnDate_ReturnsOnlyActive() {
        Employee employee = employee(1L);
        LocalDate date = LocalDate.of(2026, 6, 1);
        List<TaxDeduction> active = List.of(standardSelfDeduction(10L, employee));
        when(deductionRepository.findActiveOnDate(1L, date)).thenReturn(active);

        List<DeductionResult> result = deductionService.findActiveOnDate(1L, date);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().active()).isTrue();
        verify(deductionRepository).findActiveOnDate(1L, date);
    }

    @Test
    void findActiveOnDate_NullDate_UsesToday() {
        LocalDate today = LocalDate.now();
        when(deductionRepository.findActiveOnDate(1L, today)).thenReturn(List.of());

        deductionService.findActiveOnDate(1L, null);

        verify(deductionRepository).findActiveOnDate(1L, today);
    }

    @Test
    void findActiveOnDate_Empty_ReturnsEmpty() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        when(deductionRepository.findActiveOnDate(1L, date)).thenReturn(List.of());

        assertThat(deductionService.findActiveOnDate(1L, date)).isEmpty();
    }
}