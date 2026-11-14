package com.example.backend.payroll.api.dto.employer;

import com.example.backend.payroll.domain.shared.model.DateRange;

public record ChangeEmployerContributionPeriodCommand(DateRange validPeriod) {
}
