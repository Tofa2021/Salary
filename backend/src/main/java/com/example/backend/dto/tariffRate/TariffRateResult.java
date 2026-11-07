package com.example.backend.dto.tariffRate;

import com.example.backend.model.employee.TariffRate;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TariffRateResult(Long id, BigDecimal baseRate, LocalDate effectiveFrom, LocalDate effectiveTo,
                               String legalAct, boolean active) {
    public static TariffRateResult fromTariffRate(TariffRate rate, LocalDate today) {
        boolean active = !rate.getEffectiveFrom().isAfter(today)
                && (rate.getEffectiveTo() == null || !rate.getEffectiveTo().isBefore(today));
        
        return new TariffRateResult(
                rate.getId(),
                rate.getBaseRate(),
                rate.getEffectiveFrom(),
                rate.getEffectiveTo(),
                rate.getLegalAct(),
                active
        );
    }
}
