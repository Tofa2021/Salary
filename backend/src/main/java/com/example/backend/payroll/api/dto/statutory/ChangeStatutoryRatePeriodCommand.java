package com.example.backend.payroll.api.dto.statutory;

import com.example.backend.payroll.domain.shared.model.DateRange;

public record ChangeStatutoryRatePeriodCommand(DateRange validPeriod) {
}
