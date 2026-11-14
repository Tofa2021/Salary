package com.example.backend.payroll.statutory;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.statutory.model.StatutoryLine;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.model.StatutoryResult;
import com.example.backend.payroll.domain.statutory.policy.IncomeTaxPolicy;
import com.example.backend.payroll.domain.statutory.policy.SocialContributionPolicy;
import com.example.backend.payroll.domain.statutory.service.StatutoryCalculator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatutoryCalculatorTest {
    private static final Currency BYN = Currency.getInstance("BYN");

    @Mock
    private IncomeTaxPolicy incomeTaxPolicy;

    @Mock
    private SocialContributionPolicy socialPolicy;

    @InjectMocks
    private StatutoryCalculator calculator;

    private static Money money(String amount) {
        return Money.of(new BigDecimal(amount), BYN);
    }

    @Test
    void includesBothLinesWhenNonZero() {
        Money gross = money("2500.00");
        StatutoryRate incomeTaxRate = mock(StatutoryRate.class);
        StatutoryRate socialRate = mock(StatutoryRate.class);

        when(incomeTaxPolicy.calculate(gross, incomeTaxRate)).thenReturn(money("325.00"));
        when(socialPolicy.calculate(gross, socialRate)).thenReturn(money("25.00"));

        StatutoryResult result = calculator.calculate(gross, incomeTaxRate, socialRate);

        assertThat(result.tax()).isEqualTo(money("325.00"));
        assertThat(result.socialEmployee()).isEqualTo(money("25.00"));
        assertThat(result.total()).isEqualTo(money("350.00"));
        assertThat(result.lines())
                .extracting(StatutoryLine::code, StatutoryLine::amount)
                .containsExactly(
                        tuple("INCOME_TAX", money("325.00")),
                        tuple("SOCIAL_EMPLOYEE", money("25.00"))
                );
        assertThat(result.lines())
                .extracting(StatutoryLine::description)
                .allMatch(desc -> desc != null && !desc.isBlank());
    }

    @Test
    void omitsZeroTaxLine() {
        Money gross = money("500.00");
        StatutoryRate incomeTaxRate = mock(StatutoryRate.class);
        StatutoryRate socialRate = mock(StatutoryRate.class);

        when(incomeTaxPolicy.calculate(gross, incomeTaxRate)).thenReturn(money("0.00"));
        when(socialPolicy.calculate(gross, socialRate)).thenReturn(money("5.00"));

        StatutoryResult result = calculator.calculate(gross, incomeTaxRate, socialRate);

        assertThat(result.tax()).isEqualTo(money("0.00"));
        assertThat(result.socialEmployee()).isEqualTo(money("5.00"));
        assertThat(result.lines())
                .extracting(StatutoryLine::code, StatutoryLine::amount)
                .containsExactly(tuple("SOCIAL_EMPLOYEE", money("5.00")));
    }

    @Test
    void omitsAllLinesWhenBothAreZero() {
        Money gross = money("0.00");
        StatutoryRate incomeTaxRate = mock(StatutoryRate.class);
        StatutoryRate socialRate = mock(StatutoryRate.class);

        when(incomeTaxPolicy.calculate(gross, incomeTaxRate)).thenReturn(money("0.00"));
        when(socialPolicy.calculate(gross, socialRate)).thenReturn(money("0.00"));

        StatutoryResult result = calculator.calculate(gross, incomeTaxRate, socialRate);

        assertThat(result.tax()).isEqualTo(money("0.00"));
        assertThat(result.socialEmployee()).isEqualTo(money("0.00"));
        assertThat(result.total()).isEqualTo(money("0.00"));
        assertThat(result.lines()).isEmpty();
    }
}
