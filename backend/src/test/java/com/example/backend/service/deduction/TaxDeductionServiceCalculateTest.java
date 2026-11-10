package com.example.backend.service.deduction;

import com.example.backend.employee.domain.model.Employee;
import com.example.backend.payroll.model.TaxDeduction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TaxDeductionServiceCalculateTest extends BaseTaxDeductionServiceTest {

    @Test
    void calculateTotalDeductions_SumsActive() {
        Employee employee = employee(1L);
        LocalDate date = LocalDate.of(2026, 6, 1);
        List<TaxDeduction> active = List.of(
                standardSelfDeduction(10L, employee),
                childDeduction(11L, employee, "DOC-CHILD")
        );
        when(deductionRepository.findActiveOnDate(1L, date)).thenReturn(active);

        BigDecimal total = deductionService.calculateTotalDeductions(
                1L, date, BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));

        assertThat(total).isEqualByComparingTo("400");
    }

    @Test
    void calculateTotalDeductions_StandardSelfIncomeAboveLimit_Excluded() {
        Employee employee = employee(1L);
        LocalDate date = LocalDate.of(2026, 6, 1);
        List<TaxDeduction> active = List.of(
                standardSelfDeduction(10L, employee),
                childDeduction(11L, employee, "DOC-CHILD")
        );
        when(deductionRepository.findActiveOnDate(1L, date)).thenReturn(active);

        BigDecimal total = deductionService.calculateTotalDeductions(
                1L, date, BigDecimal.valueOf(2500), BigDecimal.valueOf(2000));

        assertThat(total).isEqualByComparingTo("200");
    }

    @Test
    void calculateTotalDeductions_noDeductions_returnsZero() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        when(deductionRepository.findActiveOnDate(1L, date)).thenReturn(List.of());

        BigDecimal total = deductionService.calculateTotalDeductions(
                1L, date, BigDecimal.valueOf(1000), BigDecimal.valueOf(2000));

        assertThat(total).isEqualByComparingTo("0");
    }
}