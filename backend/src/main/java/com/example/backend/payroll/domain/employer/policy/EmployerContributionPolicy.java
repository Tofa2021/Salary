package com.example.backend.payroll.domain.employer.policy;

import com.example.backend.payroll.domain.employer.model.EmployerContribution;
import com.example.backend.payroll.domain.employer.model.EmployerLine;
import com.example.backend.payroll.domain.employer.model.EmployerResult;
import com.example.backend.payroll.domain.shared.model.Money;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployerContributionPolicy {
    public EmployerResult calculate(Money gross, List<EmployerContribution> contributions, LocalDate date) {
        if (gross == null) {
            throw new IllegalArgumentException("gross is required");
        }
        if (contributions == null) {
            contributions = List.of();
        }

        List<EmployerLine> lines = new ArrayList<>();
        Money total = Money.zero(gross.currency());

        for (EmployerContribution contribution : contributions) {
            if (!contribution.appliesTo(date)) {
                continue;
            }

            Money amount = gross.multiply(contribution.getRate());
            lines.add(new EmployerLine(contribution.getCode(), description(contribution.getCode()), amount));
            total = total.add(amount);
        }

        Money cost = gross.add(total);
        return new EmployerResult(lines, total, cost);
    }

    private String description(String code) {
        return switch (code) {
            case "FSZN_EMPLOYER" -> "ФСЗН (34%, работодатель)";
            case "BELGOSSTRAKH" -> "Белгосстрах (0.6%)";
            default -> code;
        };
    }
}
