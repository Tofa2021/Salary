package com.example.backend.payroll.deduction;

import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.deduction.policy.DeductionPolicy;
import com.example.backend.payroll.domain.deduction.service.DeductionCalculator;
import com.example.backend.payroll.domain.shared.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductionCalculatorTest {

    private static final Currency BYN = Currency.getInstance("BYN");

    @Mock
    private DeductionPolicy deductionPolicy;

    @InjectMocks
    private DeductionCalculator deductionCalculator;

    @Test
    void delegatesToPolicy() {
        Money gross = Money.of(new BigDecimal("3000.00"), BYN);
        Money tax = Money.of(new BigDecimal("390.00"), BYN);
        List<Deduction> deductions = List.of(mock(Deduction.class));
        LocalDate date = LocalDate.of(2026, 10, 31);
        List<DeductionResult> expected = List.of(
                new DeductionResult("UNION", "Union fee", Money.of(new BigDecimal("30.00"), BYN))
        );

        when(deductionPolicy.calculate(gross, tax, deductions, date)).thenReturn(expected);

        List<DeductionResult> actual = deductionCalculator.calculate(gross, tax, deductions, date);

        assertThat(actual).isEqualTo(expected);
        verify(deductionPolicy).calculate(gross, tax, deductions, date);
    }
}
