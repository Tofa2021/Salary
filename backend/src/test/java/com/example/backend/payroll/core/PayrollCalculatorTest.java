package com.example.backend.payroll.core;

import com.example.backend.payroll.domain.accrual.model.AccrualResult;
import com.example.backend.payroll.domain.accrual.model.BonusRule;
import com.example.backend.payroll.domain.accrual.service.AccrualCalculator;
import com.example.backend.payroll.domain.base.model.SalesSummary;
import com.example.backend.payroll.domain.base.model.TariffRate;
import com.example.backend.payroll.domain.base.model.WorkVolumeSummary;
import com.example.backend.payroll.domain.base.service.BaseSalaryCalculator;
import com.example.backend.payroll.domain.core.model.Payroll;
import com.example.backend.payroll.domain.core.model.PayrollInput;
import com.example.backend.payroll.domain.core.model.PayrollStatus;
import com.example.backend.payroll.domain.core.service.PayrollCalculator;
import com.example.backend.payroll.domain.deduction.model.Deduction;
import com.example.backend.payroll.domain.deduction.model.DeductionResult;
import com.example.backend.payroll.domain.deduction.service.DeductionCalculator;
import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.model.EmployerResult;
import com.example.backend.payroll.domain.employer.service.EmployerCalculator;
import com.example.backend.payroll.domain.net.service.NetCalculator;
import com.example.backend.payroll.domain.shared.model.HoursSummary;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Period;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.model.StatutoryResult;
import com.example.backend.payroll.domain.statutory.service.StatutoryCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollCalculatorTest {
    private static final Currency BYN = Currency.getInstance("BYN");
    private static final Long EMPLOYEE_ID = 7L;
    private static final LocalDate PERIOD_START = LocalDate.of(2026, 10, 1);
    private static final LocalDate PERIOD_END = LocalDate.of(2026, 10, 31);
    private static final Instant FIXED_INSTANT = Instant.parse("2026-11-01T08:15:30Z");

    @Mock
    private BaseSalaryCalculator baseCalculator;

    @Mock
    private AccrualCalculator accrualCalculator;

    @Mock
    private StatutoryCalculator statutoryCalculator;

    @Mock
    private DeductionCalculator deductionCalculator;

    @Mock
    private NetCalculator netCalculator;

    @Mock
    private EmployerCalculator employerCalculator;

    private PayrollCalculator payrollCalculator;

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @BeforeEach
    void setUp() {
        payrollCalculator = new PayrollCalculator(
                baseCalculator,
                accrualCalculator,
                statutoryCalculator,
                deductionCalculator,
                netCalculator,
                employerCalculator
        );
    }

    @Test
    void calculatesCompletePayroll() {
        Period period = Period.of(2026, 10);
        HoursSummary hours = mock(HoursSummary.class);
        BigDecimal normHours = new BigDecimal("168");
        WorkVolumeSummary volume = mock(WorkVolumeSummary.class);
        SalesSummary sales = mock(SalesSummary.class);
        TariffRate rate = mock(TariffRate.class);
        BonusRule bonusRule = mock(BonusRule.class);
        StatutoryRate incomeTaxRate = mock(StatutoryRate.class);
        StatutoryRate socialRate = mock(StatutoryRate.class);
        List<Deduction> deductionsInput = List.of(mock(Deduction.class));
        List<EmployerContribution> employerContributionsInput = List.of(mock(EmployerContribution.class));
        Clock clock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);

        PayrollInput input = new PayrollInput(
                EMPLOYEE_ID, period, hours, normHours, volume, sales,
                rate, bonusRule, incomeTaxRate, socialRate,
                deductionsInput, employerContributionsInput, clock
        );

        Money base = money("2000.00");
        Money hourlyRate = money("11.90");
        Money accrualTotal = money("500.00");
        Money expectedGross = money("2500.00");
        Money tax = money("325.00");
        Money socialEmployee = money("25.00");
        List<DeductionResult> deductionResults = List.of(mock(DeductionResult.class));
        Money net = money("2050.00");
        Money totalEmployerContributions = money("850.00");
        Money employerCost = money("3350.00");

        AccrualResult accrualResult = mock(AccrualResult.class);
        when(accrualResult.total()).thenReturn(accrualTotal);

        StatutoryResult statutoryResult = mock(StatutoryResult.class);
        when(statutoryResult.tax()).thenReturn(tax);
        when(statutoryResult.socialEmployee()).thenReturn(socialEmployee);

        EmployerResult employerResult = mock(EmployerResult.class);
        when(employerResult.totalContributions()).thenReturn(totalEmployerContributions);
        when(employerResult.employerCost()).thenReturn(employerCost);

        when(baseCalculator.calculate(rate, hours, volume, sales, normHours)).thenReturn(base);
        when(rate.hourlyRate(normHours)).thenReturn(hourlyRate);
        when(accrualCalculator.calculate(base, hourlyRate, hours, bonusRule)).thenReturn(accrualResult);
        when(statutoryCalculator.calculate(expectedGross, incomeTaxRate, socialRate)).thenReturn(statutoryResult);
        when(deductionCalculator.calculate(expectedGross, tax, deductionsInput, PERIOD_END)).thenReturn(deductionResults);
        when(netCalculator.calculate(expectedGross, tax, socialEmployee, deductionResults)).thenReturn(net);
        when(employerCalculator.calculate(expectedGross, employerContributionsInput, PERIOD_END)).thenReturn(employerResult);

        Payroll result = payrollCalculator.calculate(input);

        assertThat(result.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
        assertThat(result.getPeriod()).isEqualTo(period);
        assertThat(result.getStatus()).isEqualTo(PayrollStatus.CALCULATED);
        assertThat(result.getBase()).isEqualTo(base);
        assertThat(result.getGross()).isEqualTo(expectedGross);
        assertThat(result.getTax()).isEqualTo(tax);
        assertThat(result.getSocialEmployee()).isEqualTo(socialEmployee);
        assertThat(result.getDeductions()).containsExactlyElementsOf(deductionResults);
        assertThat(result.getNet()).isEqualTo(net);
        assertThat(result.getEmployerContributions()).isEqualTo(totalEmployerContributions);
        assertThat(result.getEmployerCost()).isEqualTo(employerCost);
        assertThat(result.getCalculatedAt()).isEqualTo(LocalDateTime.ofInstant(FIXED_INSTANT, ZoneOffset.UTC));

        verify(statutoryCalculator).calculate(expectedGross, incomeTaxRate, socialRate);
        verify(deductionCalculator).calculate(expectedGross, tax, deductionsInput, PERIOD_END);
        verify(netCalculator).calculate(expectedGross, tax, socialEmployee, deductionResults);
        verify(employerCalculator).calculate(expectedGross, employerContributionsInput, PERIOD_END);
    }
}
