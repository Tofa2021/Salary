package com.example.backend.payroll.domain.statutory.service;

import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.statutory.model.StatutoryLine;
import com.example.backend.payroll.domain.statutory.model.StatutoryRate;
import com.example.backend.payroll.domain.statutory.model.StatutoryResult;
import com.example.backend.payroll.domain.statutory.policy.IncomeTaxPolicy;
import com.example.backend.payroll.domain.statutory.policy.SocialContributionPolicy;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class StatutoryCalculator {
    private final IncomeTaxPolicy incomeTaxPolicy;
    private final SocialContributionPolicy socialPolicy;

    public StatutoryResult calculate(Money gross, StatutoryRate incomeTaxRate, StatutoryRate socialRate) {
        List<StatutoryLine> lines = new ArrayList<>();

        Money tax = incomeTaxPolicy.calculate(gross, incomeTaxRate);
        if (!tax.isZero()) {
            lines.add(new StatutoryLine("INCOME_TAX", "Подоходный налог (13%)", tax));
        }

        Money social = socialPolicy.calculate(gross, socialRate);
        if (!social.isZero()) {
            lines.add(new StatutoryLine("SOCIAL_EMPLOYEE", "ФСЗН (1%)", social));
        }

        return new StatutoryResult(tax, social, lines);
    }
}
