package com.example.backend.payroll.api.dto.statutory;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.Percentage;

public record CreateStatutoryRateCommand(
        String code,
        Percentage rate,
        Money threshold,
        DateRange validPeriod
) {
}
