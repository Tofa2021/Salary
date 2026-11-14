package com.example.backend.payroll.api.dto.employer;

import com.example.backend.payroll.domain.shared.model.Percentage;

public record ChangeEmployerContributionRateCommand(Percentage rate) {
}
