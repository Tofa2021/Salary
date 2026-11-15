package com.example.backend.payroll.api.dto.rate;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.Money;
import com.example.backend.payroll.domain.shared.model.RateType;

public record TariffRateResult(
        Long id,
        Long positionId,
        Money amount,
        RateType type,
        DateRange validPeriod
) {
}
