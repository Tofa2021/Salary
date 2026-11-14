package com.example.backend.payroll.employer;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.model.EmployerLine;
import com.example.backend.payroll.domain.employer.model.EmployerResult;
import com.example.backend.payroll.domain.employer.policy.EmployerContributionPolicy;
import com.example.backend.payroll.domain.employer.service.EmployerCalculator;
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
class EmployerCalculatorTest {

    private static final Currency BYN = Currency.getInstance("BYN");

    @Mock
    private EmployerContributionPolicy policy;

    @InjectMocks
    private EmployerCalculator calculator;

    @Test
    void delegatesToPolicy() {
        Money gross = Money.of(new BigDecimal("2000.00"), BYN);
        List<EmployerContribution> contributions = List.of(mock(EmployerContribution.class));
        LocalDate date = LocalDate.of(2026, 10, 31);
        EmployerResult expected = new EmployerResult(
                List.of(new EmployerLine("FSZN_EMPLOYER", "ФСЗН", Money.of(new BigDecimal("680.00"), BYN))),
                Money.of(new BigDecimal("680.00"), BYN),
                Money.of(new BigDecimal("2680.00"), BYN)
        );

        when(policy.calculate(gross, contributions, date)).thenReturn(expected);

        EmployerResult actual = calculator.calculate(gross, contributions, date);

        assertThat(actual).isEqualTo(expected);
        verify(policy).calculate(gross, contributions, date);
    }
}
