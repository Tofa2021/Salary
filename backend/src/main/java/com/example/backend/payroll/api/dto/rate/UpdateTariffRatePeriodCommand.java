package com.example.backend.payroll.api.dto.rate;

import com.example.backend.payroll.domain.shared.model.DateRange;

public record UpdateTariffRatePeriodCommand(DateRange validPeriod) {
}
