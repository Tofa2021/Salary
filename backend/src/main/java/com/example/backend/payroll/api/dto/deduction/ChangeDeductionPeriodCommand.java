package com.example.backend.payroll.api.dto.deduction;

import com.example.backend.payroll.domain.shared.model.DateRange;

public record ChangeDeductionPeriodCommand(DateRange validPeriod) {
}
