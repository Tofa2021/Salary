package com.example.backend.payroll.api.dto.rate;

import com.example.backend.payroll.domain.shared.model.DateRange;
import com.example.backend.payroll.domain.shared.model.RateType;

import java.math.BigDecimal;

public record CreateTariffRateCommand(
        Long positionId,
        BigDecimal amount,
        String currency,
        RateType type,
        DateRange validPeriod
) {
}
