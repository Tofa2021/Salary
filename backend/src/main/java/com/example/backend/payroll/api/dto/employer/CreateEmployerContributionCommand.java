package com.example.backend.payroll.api.dto.employer;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Percentage;

public record CreateEmployerContributionCommand(
        String code,
        Percentage rate,
        DateRange validPeriod
) {
}
