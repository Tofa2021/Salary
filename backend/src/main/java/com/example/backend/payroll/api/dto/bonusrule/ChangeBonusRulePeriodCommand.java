package com.example.backend.payroll.api.dto.bonusrule;

import com.example.backend.payroll.domain.shared.model.DateRange;

public record ChangeBonusRulePeriodCommand(DateRange validPeriod) {
}
